package com.example.Calayo.helper;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.Calayo.acts.order_Details;
import com.example.Calayo.adapters.addOns;
import com.example.Calayo.adapters.address_adapter;
import com.example.Calayo.adapters.product_adapt;
import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.address;
import com.example.Calayo.entities.adds;
import com.example.Calayo.entities.cartItem;
import com.example.Calayo.entities.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Singleton class to temporarily hold in-memory data for a food delivery app session.
 * This storage handles cart items, add-ons, user addresses, and checkout orders.
 * It supports LiveData observation and syncs updates to a remote Firestore DB via WorkManager.
 */
public class tempStorage {
    private static final String TAG = "TempStorage";

    // In-memory storage lists
    private final ArrayList<Item.addOn> addOnArrayList;
    private final ArrayList<Item> itemArrayList;
    private final ArrayList<cartItem> cartItemArrayList;
    private  ArrayList<Order> pendingArrayList;
    private  ArrayList<Order> approvedArrayList;
    private  ArrayList<Order> deliveryArrayList;
    private  ArrayList<Order> receivedArrayList;
    private final ArrayList<address> addressList;
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();
    ArrayList<Item.addOn> addOnsItems;
    ArrayList<adds> adds ;
    SharedPreferences sharedPreferences;
    private String loggedIn;

    // LiveData for UI observation of cart changes
    private final MutableLiveData<ArrayList<cartItem>> cartLiveData;

    // Firebase Firestore instance for remote sync
    private final FirebaseFirestore db;
    private boolean isloggedIn;
    private cartItem selectedCartItem;
    private Item searchResult;
    /**
     * Private constructor initializes all internal lists and Firestore reference.
     */
    private tempStorage() {

        db = FirebaseFirestore.getInstance();
        addOnArrayList = new ArrayList<>();
        cartItemArrayList = new ArrayList<>();
        itemArrayList = new ArrayList<>();
        adds = new ArrayList<>();
        pendingArrayList = new ArrayList<>();
        approvedArrayList = new ArrayList<>();
        deliveryArrayList = new ArrayList<>();
        receivedArrayList = new ArrayList<>();
        addressList = new ArrayList<>();
        cartLiveData = new MutableLiveData<>();
        addOnsItems =  new ArrayList<>();
        Log.d(TAG, "Temporary storage initialized.");
    }

    public boolean getIsLoggedin(){
        return isloggedIn;
    }
    public String getLoggedin(){
        return loggedIn;
    }
    public void setLoggedin(String s){
        loggedIn = s;
    }


    public ArrayList<address> getAddressList() {
        return addressList;
    }

    public ArrayList<Order> getPendingArrayList() {
        return pendingArrayList;
    }
    public ArrayList<Order> getApprovedArrayList() {
        return approvedArrayList;
    }
    public ArrayList<Order> getDeliveryArrayList() {
        return deliveryArrayList;
    }
    public ArrayList<Order> getReceivedArrayList() {
        return receivedArrayList;
    }


    public ArrayList<Item> getItemArrayList() {
        return itemArrayList;
    }
    public ArrayList<adds> getAddsArrayList() {
        return adds;
    }

    public void setIsloggedIn(boolean isloggedIn) {
        this.isloggedIn = isloggedIn;
    }

    public Item getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(Item searchResult) {
        this.searchResult = searchResult;
    }

    public cartItem getSelectedCartItem() {
        return selectedCartItem;
    }

    public void setSelectedCartItem(cartItem selectedCartItem) {
        this.selectedCartItem = selectedCartItem;
    }

    /**
     * Thread-safe Singleton holder pattern
     */
    private static final class InstanceHolder {
        private static final tempStorage instance = new tempStorage();
    }

    /**
     * Returns the singleton instance of tempStorage
     */
    public static tempStorage getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * Returns LiveData for cart item list, used for observing changes in UI.
     */
    public LiveData<ArrayList<cartItem>> getCartLiveData() {
        return cartLiveData;
    }

    /**
     * Returns the in-memory cart item list.
     */
    public ArrayList<cartItem> getCartItemArrayList() {
        return cartItemArrayList;
    }


    public void addCartItem(cartItem item) {
        int index = findNodeInsertion(item);
        cartItemArrayList.add(index, item);
        Log.i(TAG, "Item '" + item.getName() + "' added at index " + index);
        cartLiveData.postValue(cartItemArrayList);
//        syncCartToRemote(context);
    }
    /**
     * Adds an item to the cart list in sorted order and triggers remote sync.
     * @param context Application context for WorkManager
     * @param item cart item to add
     */
    public void addCartItem(Context context, cartItem item) {
        int index = findNodeInsertion(item);
        cartItemArrayList.add(index, item);
        Log.i(TAG, "Item '" + item.getName() + "' added at index " + index);
        cartLiveData.postValue(cartItemArrayList);
//        syncCartToRemote(context);
    }

    /**
     * Replaces the entire cart list and pushes to remote.
     * @param context Application context
     * @param list New cart item list
     */
    public void setCartItemArrayList(Context context, ArrayList<cartItem> list) {
        cartItemArrayList.clear();
        cartItemArrayList.addAll(list);
        Log.d(TAG, "Cart items replaced. Size: " + list.size());
        cartLiveData.postValue(cartItemArrayList);
//        syncCartToRemote(context);
    }
    public void deleteItem( String id) {
        cartItem item = searchCartItem(id);
        if (item != null) {
            cartItemArrayList.remove(item);
            Log.i(TAG, "Item '" + id + "' removed from cart.");
            cartLiveData.postValue(cartItemArrayList);
//            syncCartToRemote(context);
        } else {
            Log.w(TAG, "Attempted to remove non-existing item '" + id + "'");
        }
    }

    /**
     * Removes a cart item by name if it exists, triggers sync.
     * @param context Application context
     * @param name Name of the item to delete
     */
    public void deleteItem(Context context, String name) {
        cartItem item = searchCartItem(name);
        if (item != null) {
            cartItemArrayList.remove(item);
            Log.i(TAG, "Item '" + name + "' removed from cart.");
            cartLiveData.postValue(cartItemArrayList);
//            syncCartToRemote(context);
        } else {
            Log.w(TAG, "Attempted to remove non-existing item '" + name + "'");
        }
    }

    /**
     * Determines the sorted index for a new cart item based on its date.
     * @param target New cart item
     * @return Index where item should be inserted
     */
    public int findNodeInsertion(cartItem target) {
        int left = 0;
        int right = cartItemArrayList.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = cartItemArrayList.get(mid).getDate().compareTo(target.getDate());
            if (cmp == 0) return mid;
            else if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return left;
    }

    /**
     * Searches for a cart item by its name.
     * @param id ID to search
     * @return cartItem object or null if not found
     */
    public cartItem searchCartItem(String id) {
        for (cartItem item : cartItemArrayList) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
    public Item searchItem(String name) {
        for (Item item : itemArrayList) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns the list of selected add-ons.
     */
    public ArrayList<Item.addOn> getAddOnArrayList() {
        return addOnArrayList;
    }

    /**
     * Replaces the entire add-on list.
     * @param list New add-on list
     */
    public void setAddOnArrayList(ArrayList<Item.addOn> list) {
        addOnArrayList.clear();
        addOnArrayList.addAll(list);
        Log.d(TAG, "Add-on list updated. Size: " + list.size());
    }

    /**
     * Computes total price of all selected add-ons.
     * @return total add-on price
     */
    public double getTotalAddOnPrice() {
        double total = 0.0;
        for (Item.addOn addOn : addOnArrayList) {
            total += addOn.getAddOnPrice();
        }
        Log.d(TAG, "Total add-on price: ₱" + total);
        return total;
    }

    /**
     * Enqueues a WorkManager job to sync cart data to Firestore.
     * This uses OneTimeWorkRequest with network constraints.
     * @param context Application context
     */
//    public void syncCartToRemote(Context context) {
//        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();
//
//        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(SyncCartWorker.class)
//                .setConstraints(constraints)
//                .build();
//
//        WorkManager.getInstance(context).enqueue(syncRequest);
//        Log.i(TAG, "Sync request enqueued to WorkManager.");
//    }
    public interface OnTempDataReadyListener {
        void onReady();
    }
    public void loadAllData(OnTempDataReadyListener listener) {
        final int totalTasks = 2;
        final int[] completedTasks = {0};

        Runnable checkDone = () -> {
            completedTasks[0]++;
            if (completedTasks[0] == totalTasks && listener != null) {
                listener.onReady();
            }
        };

        // Products
        db.collection("items").get().addOnSuccessListener(itemSnapshots -> {
            itemArrayList.clear(); // Optional: clear before adding
            for (DocumentSnapshot itemDoc : itemSnapshots) {
                Item item = itemDoc.toObject(Item.class);
                String itemId = itemDoc.getId();
                if (item != null) {
                    db.collection("items").document(itemId).collection("addOns")
                            .get()
                            .addOnSuccessListener(addOnSnapshots -> {
                                ArrayList<Item.addOn> addOnList = new ArrayList<>();
                                for (DocumentSnapshot addOnDoc : addOnSnapshots) {
                                    Item.addOn addOn = addOnDoc.toObject(Item.addOn.class);
                                    addOnList.add(addOn);
                                }
                                item.setAddOns(addOnList);
                                itemArrayList.add(item);
                                if (itemArrayList.size() == itemSnapshots.size()) {
                                    checkDone.run();
                                }
                            });
                }
            }
        });

        // Adds
        db.collection("adds").get().addOnSuccessListener(snapshots -> {
            adds.clear();
            for (DocumentSnapshot doc : snapshots) {
                adds item = doc.toObject(adds.class);
                adds.add(item);
            }
            checkDone.run();
        });
    }
    public void loadAllUserData(OnTempDataReadyListener listener) {
        final int totalTasks = 3;
        final int[] completedTasks = {0};

        Runnable checkDone = () -> {
            completedTasks[0]++;
            if (completedTasks[0] == totalTasks && listener != null) {
                listener.onReady();
            }
        };

        // Addresses
        db.collection("users").document(loggedIn).collection("address")
                .get()
                .addOnSuccessListener(snapshots -> {
                    addressList.clear();
                    for (DocumentSnapshot doc : snapshots) {
                        address add = doc.toObject(address.class);
                        addressList.add(add);
                    }
                    checkDone.run();
                });
        //Food Cart Items
        db.collection("users").document(loggedIn).collection("Food Cart Items")
                .get()
                .addOnSuccessListener(snapshots -> {
                    cartItemArrayList.clear();
                    for (DocumentSnapshot doc : snapshots) {
                        cartItem add = doc.toObject(cartItem.class);
                        cartItemArrayList.add(add);
                    }
                    checkDone.run();
                });

        // Orders
        db.collection("users").document(loggedIn).collection("Orders")
                .get()
                .addOnSuccessListener(snapshots -> {
                    // Clear previous data
                    pendingArrayList.clear();
                    approvedArrayList.clear();
                    deliveryArrayList.clear();
                    receivedArrayList.clear();

                    for (DocumentSnapshot doc : snapshots) {
                        Order add = doc.toObject(Order.class);

                        if (add == null || add.getStatus() == null) continue;

                        switch (add.getStatus()) {
                            case "Pending":
                                pendingArrayList.add(add);
                                break;
                            case "Approved":
                                approvedArrayList.add(add);
                                break;
                            case "Deliver":
                                deliveryArrayList.add(add);
                                break;
                            case "Received":
                                receivedArrayList.add(add);
                                break;
                            default:
                                Log.w("Firestore", "Unknown status: " + add.getStatus());
                                break;
                        }
                    }

                    // Callback when done
                    checkDone.run();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error loading orders", e);
                    checkDone.run(); // Optionally still run the callback
                });
    }


    }
