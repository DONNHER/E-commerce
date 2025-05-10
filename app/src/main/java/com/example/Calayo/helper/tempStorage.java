package com.example.Calayo.helper;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.Calayo.entities.Item;
import com.example.Calayo.entities.Order;
import com.example.Calayo.entities.address;
import com.example.Calayo.entities.cartItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Singleton class to temporarily hold in-memory data for a food delivery app session.
 * This storage handles cart items, add-ons, user addresses, and checkout orders.
 * It supports LiveData observation and syncs updates to a remote Firestore DB via WorkManager.
 */
public class tempStorage {
    private static final String TAG = "TempStorage";

    // In-memory storage lists
    private final ArrayList<Item.addOn> addOnArrayList;
    private final ArrayList<cartItem> cartItemArrayList;
    private final ArrayList<Order> checkOutArrayList;
    private final ArrayList<address> addressList;

    // LiveData for UI observation of cart changes
    private final MutableLiveData<ArrayList<cartItem>> cartLiveData;

    // Firebase Firestore instance for remote sync
    private final FirebaseFirestore db;

    /**
     * Private constructor initializes all internal lists and Firestore reference.
     */
    private tempStorage() {
        addOnArrayList = new ArrayList<>();
        cartItemArrayList = new ArrayList<>();
        checkOutArrayList = new ArrayList<>();
        addressList = new ArrayList<>();
        cartLiveData = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Temporary storage initialized.");
    }

    public ArrayList<Order> getCheckOutArrayList() {
        return checkOutArrayList;
    }

    public ArrayList<address> getAddressList() {
        return addressList;
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
    public void deleteItem( String name) {
        cartItem item = searchItem(name);
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
     * Removes a cart item by name if it exists, triggers sync.
     * @param context Application context
     * @param name Name of the item to delete
     */
    public void deleteItem(Context context, String name) {
        cartItem item = searchItem(name);
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
     * @param name Name to search
     * @return cartItem object or null if not found
     */
    public cartItem searchItem(String name) {
        for (cartItem item : cartItemArrayList) {
            if (item.getName().equals(name)) {
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

}
