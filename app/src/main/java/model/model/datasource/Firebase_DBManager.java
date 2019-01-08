package model.model.datasource;

import android.support.annotation.NonNull;
import android.widget.LinearLayout;

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
import java.util.stream.Collectors;

import model.model.backend.Backend;
import model.model.entities.Driver;
import model.model.entities.Ride;

import static com.pickapp.driverapp.LoginActivity.Password;

public class Firebase_DBManager implements Backend {

    public Firebase_DBManager() {

        this.notifyToDriverList(new NotifyDataChange<List<Driver>>() {
            @Override
            public void OnDataChanged(List<Driver> obj) {
                if (DriverList != obj) {
                    DriverList = obj;
                }
            }

            @Override
            public void onFailure(Exception exception) {
            }
        });

        this.notifyToRideList(new NotifyDataChange<List<Ride>>() {
            @Override
            public void OnDataChanged(List<Ride> obj) {
                if (RideList != obj) {
                    RideList = obj;
                }
            }

            @Override
            public void onFailure(Exception exception) {
            }
        });
    }

    static DatabaseReference DriversRef;
    static List<Driver> DriverList;

    static {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DriversRef = database.getReference("Drivers");
        DriverList = new ArrayList<>();
    }


    public void addDriver(final Driver Driver, final Action action) {
        DriversRef.push().setValue(Driver).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess();
                action.onProgress("uploading Driver data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("error upload Driver data", 100);

            }
        });
    }

    public void removeDriver(final String key, final Action action) {

        DriversRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Driver value = dataSnapshot.getValue(Driver.class);
                if (value == null)
                    action.onFailure(new Exception("Driver not found ..."));
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

        removeDriver(toUpdate.getHashId(), new Action<Long>() {
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
        if (notifyDataChange != null) {
            if (DriverRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify Driver list"));
                return;
            }
            DriverList.clear();

            DriverRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Driver driver = dataSnapshot.getValue(Driver.class);
                    driver.setHashId(dataSnapshot.getKey());
                    DriverList.add(driver);

                    notifyDataChange.OnDataChanged(DriverList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Driver driver = dataSnapshot.getValue(Driver.class);
                    driver.setHashId(dataSnapshot.getKey());

                    for (int i = 0; i < DriverList.size(); i++) {
                        if (DriverList.get(i).getHashId().equals(driver.getHashId())) {
                            DriverList.set(i, driver);
                            break;
                        }
                    }
                    notifyDataChange.OnDataChanged(DriverList);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Driver driver = dataSnapshot.getValue(Driver.class);
                    driver.setHashId(dataSnapshot.getKey());

                    for (int i = 0; i < DriverList.size(); i++) {
                        if (DriverList.get(i).getHashId().equals(driver.getHashId())) {
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

    public Driver getDriver(String email, final String password) {

        Driver driver = new Driver();
        for (Driver d : DriverList
                ) {
            if (d.getPassword().equals(password) && d.getEmailAddress().equals(email))
                driver = d;
        }
        return driver;
        /*final Driver[] currentDriver = new Driver[1];

        Query query = DriversRef.orderByChild("emailAddress").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    currentDriver[0] = dataSnapshot.getChildren().iterator().next().getValue(Driver.class);
                else
                    currentDriver[0] = new Driver();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return currentDriver[0];*/
    }

    public void isDriversPasswordCorrect(String dEmail, final String dPassword, final Action action) {
        Query query = DriversRef.orderByChild("emailAddress").equalTo(dEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Driver currentDriver = dataSnapshot.getChildren().iterator().next().getValue(Driver.class);
                    if (currentDriver.getPassword().equals(dPassword)) {
                        action.onSuccess();
                    } else {
                        action.onFailure(new Exception("Wrong password!"));
                    }
                } else
                    action.onFailure(new Exception("This email is not registered in the system"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public int totalKmsForDriver(Driver driver) {
        int kms = 0;
        for (Ride r : RideList) {
            if (r.getStatus()==(Ride.ClientRequestStatus.CLOSED)) {
                if (r.getDriverName().equals(driver.getFirstName()))
                    kms += r.getLengthOfRide();
            }
        }

        return kms;

    }

    public int[] getMonthlyEarnings(Driver driver) {
        int monthlyEarnings[] = {0,0,0,0,0,0,0,0,0,0,0,0};
        for (Ride r : RideList) {
            if (r.getStatus()==(Ride.ClientRequestStatus.CLOSED)) {
                if (r.getDriverName().equals(driver.getFirstName()))
                    monthlyEarnings[r.getStartTime().getMonth()] += r.getLengthOfRide();
            }
        }

        return monthlyEarnings;
    }


// *********************  Rider methods for database *******************88

    static DatabaseReference RidesRef;
    static List<Ride> RideList;

    static {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        RidesRef = database.getReference("Rides");
        RideList = new ArrayList<>();
    }


    public void addRide(final Ride ride, final Action action) {
        RidesRef.child(ride.getRideId()).setValue(ride).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void removeRide(final String id, final Action action) {
        RidesRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Ride value = dataSnapshot.getValue(Ride.class);
                if (value == null)
                    action.onFailure(new Exception("Ride not found ..."));
                else {
                    RidesRef.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

        addRide(toUpdate, action);
    }
    public List<Ride> getDriverHistoryList(Driver driver) {
        List<Ride> driversRideList = new ArrayList<Ride>();
        for (Ride r : RideList
                ) {
            if (r.getStatus() == Ride.ClientRequestStatus.CLOSED)
                if (r.getDriverName().equals(driver.getFirstName()))
                    driversRideList.add(r);
        }
        return driversRideList;
    }

    public List<Ride> getWaitingList() {
        List<Ride> rideList = new ArrayList<Ride>();
        for (Ride r : RideList
                ) {
            if (r.getStatus() == Ride.ClientRequestStatus.WAITING)
                rideList.add(r);
        }
        return rideList;
    }

    public Ride getRider(String id) {
        Ride ride = new Ride();
        for (Ride r : RideList
                ) {
            if (r.getRideId().equals(id))
                ride = r;
        }
        return ride;
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
                    Ride ride = dataSnapshot.getValue(Ride.class);
                    ride.setRideId(dataSnapshot.getKey());
                    RideList.add(ride);

                    notifyDataChange.OnDataChanged(RideList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Ride Ride = dataSnapshot.getValue(Ride.class);

                    for (int i = 0; i < RideList.size(); i++) {
                        if (RideList.get(i).getRideId().equals(s)) {
                            RideList.set(i, Ride);
                            break;
                        }
                    }
                    notifyDataChange.OnDataChanged(RideList);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getKey();
                    for (int i = 0; i < RideList.size(); i++) {
                        if (RideList.get(i).getRideId().equals(id)) {
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