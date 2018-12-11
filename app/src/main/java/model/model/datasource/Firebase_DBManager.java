package model.model.datasource;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.model.backend.Backend;
import model.model.entities.Driver;
import model.model.entities.Ride;

import static com.pickapp.driverapp.LoginActivity.Password;

public class Firebase_DBManager implements Backend {


    static DatabaseReference DriversRef;
    static List<Driver> DriverList;
    static {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DriversRef = database.getReference("Drivers");
        DriverList = new ArrayList<>();
    }



    public void addDriver(final Driver Driver, final Action action) {

        String key = Driver.getId().toString();
        DriversRef.push().setValue(Driver).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess();
                action.onProgress("upload Ride data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("error upload Ride data", 100);

            }
        });
    }

    public void removeDriver(long id, final Action action) {

        final String key = ((Long) id).toString();

        DriversRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Driver value = dataSnapshot.getValue(Driver.class);
                if (value == null)
                    action.onFailure(new Exception("Driver not find ..."));
                else {
                    DriversRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            action.onSuccess();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            action.onFailure(e);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
            }
        });
    }

    public void updateRide(final Driver toUpdate, final Action action) {
        //final String key = ((Long) toUpdate.getPhone()).toString();

        removeDriver(toUpdate.getId(), new Action<Long>() {
            @Override
            public void onSuccess() {
                addDriver(toUpdate, action);
            }

            @Override
            public void onFailure(Exception exception) {
                action.onFailure(exception);
            }

            @Override
            public void onProgress(String status, double percent) {
                action.onProgress(status, percent);
            }
        });
    }

    private static ChildEventListener DriverRefChildEventListener;

    public void notifyToDriverList(final NotifyDataChange<List<Driver>> notifyDataChange) {
        if (notifyDataChange != null) {if (DriverRefChildEventListener != null) {
            notifyDataChange.onFailure(new Exception("first unNotify Driver list"));
            return;
        }
            DriverList.clear();

            DriverRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Driver Driver = dataSnapshot.getValue(Driver.class);
                    String id = dataSnapshot.getKey();
                    // Driver.setId(Long.parseLong(id));
                    DriverList.add(Driver);

                    notifyDataChange.OnDataChanged(DriverList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Driver Driver = dataSnapshot.getValue(Driver.class);
                    Long id = Long.parseLong(dataSnapshot.getKey());
                    Driver.setId(id);


                    for (int i = 0; i < DriverList.size(); i++) {
                        if (DriverList.get(i).getId().equals(id)) {
                            DriverList.set(i, Driver);
                            break;
                        }
                    }
                    notifyDataChange.OnDataChanged(DriverList);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Driver Driver = dataSnapshot.getValue(Driver.class);
                    Long id = Long.parseLong(dataSnapshot.getKey());
                    Driver.setId(id);

                    for (int i = 0; i < DriverList.size(); i++) {
                        if (DriverList.get(i).getId() == id) {
                            DriverList.remove(i);
                            break;
                        }
                    }
                    notifyDataChange.OnDataChanged(DriverList);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            DriversRef.addChildEventListener(DriverRefChildEventListener);
        }
    }

    public void stopNotifyToDriverList() {
        if (DriverRefChildEventListener != null) {
            DriversRef.removeEventListener(DriverRefChildEventListener);
            DriverRefChildEventListener = null;
        }
    }

    public void isDriverInDataBase(final Driver driver, final Action action) {
        Query query = DriversRef.orderByChild("id").equalTo(driver.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    action.onFailure(new Exception("your already have an account"));
                } else
                    action.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void isDriversPasswordCorrect(final Driver driver, final Action action) {
        Query query = DriversRef.orderByChild("email").equalTo(driver.getEmailAddress());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(dataSnapshot.getChildren().iterator().next().getValue(Driver.class).getPassword().equals( driver.getPassword()))
                        action.onSuccess();
                } else
                    action.onFailure(new Exception("This email and/or password are not registered in the system"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

// *********************  Rider methods for database *******************88

    static DatabaseReference RidesRef;
    static List<Ride> RideList;

    static {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        RidesRef = database.getReference("Rides");
        RideList = new ArrayList<>();
    }


    public void addRide(final Ride Ride, final Action action) {String key = Ride.getPassengerPhoneNumber().toString();
        RidesRef.push().setValue(Ride).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess();
                action.onProgress("upload Ride data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("error upload Ride data", 100);

            }
        });
    }

    public void removeRide(long phone, final Action action) {

        final String key = ((Long) phone).toString();

        RidesRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Ride value = dataSnapshot.getValue(Ride.class);
                if (value == null)
                    action.onFailure(new Exception("Ride not find ..."));
                else {
                    RidesRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            action.onSuccess();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            action.onFailure(e);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
            }
        });
    }

    public void updateRide(final Ride toUpdate, final Action action) {
        //final String key = ((Long) toUpdate.getPhone()).toString();

        removeRide(toUpdate.getPassengerPhoneNumber(), new Action<Long>() {
            @Override
            public void onSuccess() {
                addRide(toUpdate, action);
            }

            @Override
            public void onFailure(Exception exception) {
                action.onFailure(exception);
            }

            @Override
            public void onProgress(String status, double percent) {
                action.onProgress(status, percent);
            }
        });
    }

    private static ChildEventListener RideRefChildEventListener;

    public void notifyToRideList(final NotifyDataChange<List<Ride>> notifyDataChange) {
        if (notifyDataChange != null) {

            if (RideRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify Ride list"));
                return;
            }
            RideList.clear();

            RideRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Ride Ride = dataSnapshot.getValue(Ride.class);
                    String phone = dataSnapshot.getKey();
                    Ride.setPassengerPhoneNumber(Long.parseLong(phone));
                    RideList.add(Ride);


                    notifyDataChange.OnDataChanged(RideList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Ride Ride = dataSnapshot.getValue(Ride.class);
                    Long phone = Long.parseLong(dataSnapshot.getKey());
                    Ride.setPassengerPhoneNumber(phone);for (int i = 0; i < RideList.size(); i++) {
                        if (RideList.get(i).getPassengerPhoneNumber().equals(phone)) {
                            RideList.set(i, Ride);
                            break;
                        }
                    }
                    notifyDataChange.OnDataChanged(RideList);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Ride Ride = dataSnapshot.getValue(Ride.class);
                    Long phone = Long.parseLong(dataSnapshot.getKey());
                    Ride.setPassengerPhoneNumber(phone);

                    for (int i = 0; i < RideList.size(); i++) {
                        if (RideList.get(i).getPassengerPhoneNumber() == phone) {
                            RideList.remove(i);
                            break;
                        }
                    }
                    notifyDataChange.OnDataChanged(RideList);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            RidesRef.addChildEventListener(RideRefChildEventListener);
        }
    }

    public static void stopNotifyToRideList() {
        if (RideRefChildEventListener != null) {
            RidesRef.removeEventListener(RideRefChildEventListener);
            RideRefChildEventListener = null;
        }
    }

}