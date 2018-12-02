package model.model.datasource;

import android.support.annotation.NonNull;

import model.model.backend.Backend;
import model.model.entities.Driver;
import model.model.entities.Ride;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Firebase_DBManager implements Backend {

    public interface Action<T> {
        void onSuccess(T obj);

        void onFailure(Exception exception);

        void onProgress(String status, double percent);
    }

    public interface NotifyDataChange<T> {
        void OnDataChanged(T obj);

        void onFailure(Exception exception);
    }

    static DatabaseReference DriversRef;
    static List<Driver> DriverList;

    static {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DriversRef = database.getReference("Drivers");
        DriverList = new ArrayList<>();
    }


    public void addDriver(final Driver Driver, final Action<Long> action) {

        String key = Driver.getId().toString();
        DriversRef.push().setValue(Driver).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(Driver.getId());
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

    public static void removeDriver(long id, final Action<Long> action) {

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
                            action.onSuccess(value.getId());
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

    public void updateRide(final Driver toUpdate, final Action<Long> action) {
        //final String key = ((Long) toUpdate.getPhone()).toString();

        removeDriver(toUpdate.getId(), new Action<Long>() {
            @Override
            public void onSuccess(Long obj) {
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

    public static void notifyToRideList(final NotifyDataChange<List<Driver>> notifyDataChange) {
        if (notifyDataChange != null) {

            if (DriverRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify Driver list"));
                return;
            }
            DriverList.clear();

            DriverRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Driver Driver = dataSnapshot.getValue(Driver.class);
                    String id = dataSnapshot.getKey();
                    Driver.setId(Long.parseLong(id));
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

    public static void stopNotifyToRideList() {
        if (DriverRefChildEventListener != null) {
            DriversRef.removeEventListener(DriverRefChildEventListener);
            DriverRefChildEventListener = null;
        }
    }

}
