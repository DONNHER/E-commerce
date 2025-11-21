/// This file defines the `OrderProvider`, which is responsible for managing the user's
/// order history and real-time order tracking. It uses `ChangeNotifier` to update
/// the UI when order data changes.

import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/order.dart';

/// Manages the user's orders, including fetching history and tracking individual orders.
class OrderProvider extends ChangeNotifier {
  // An instance of the Firebase Firestore database.
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;
  // A private list to hold the user's orders.
  List<OrderModel> _orders = [];
  // A subscription to a specific order's real-time updates.
  StreamSubscription<DocumentSnapshot>? _orderSubscription;

  /// A public getter for the list of orders.
  List<OrderModel> get orders => _orders;

  /// Fetches a user's entire order history from Firestore.
  ///
  /// - [userId]: The ID of the user whose orders are to be fetched.
  Future<void> fetchOrders(String userId) async {
    try {
      final snapshot = await _firestore
          .collection('orders')
          // Filter orders by the user's ID.
          .where('userId', isEqualTo: userId)
          // Sort orders by creation date in descending order.
          .orderBy('createdAt', descending: true)
          .get();

      // Map the Firestore documents to a list of `OrderModel` objects.
      _orders = snapshot.docs.map((doc) => OrderModel.fromMap(doc.id, doc.data())).toList();
      // Notify listening widgets of the change.
      notifyListeners();
    } catch (e) {
      // Log any errors that occur during the fetch.
      print('Error fetching orders: $e');
    }
  }

  /// Cancels an order by updating its status to 'Cancelled' in Firestore.
  ///
  /// - [orderId]: The ID of the order to be cancelled.
  Future<void> cancelOrder(String orderId) async {
    try {
      // Update the order document in Firestore.
      await _firestore.collection('orders').doc(orderId).update({'status': 'Cancelled'});
      // Find the order in the local list.
      final index = _orders.indexWhere((order) => order.id == orderId);
      if (index != -1) {
        // Update the status locally to immediately reflect the change in the UI.
        _orders[index].status = 'Cancelled';
        notifyListeners();
      }
    } catch (e) {
      // Log any errors that occur during cancellation.
      print('Error cancelling order: $e');
    }
  }

  /// Subscribes to real-time updates for a single order.
  ///
  /// This is used for the order tracking feature.
  /// - [orderId]: The ID of the order to track.
  void trackOrder(String orderId) {
    // Cancel any existing subscription to avoid multiple listeners on different orders.
    _orderSubscription?.cancel();
    _orderSubscription = _firestore.collection('orders').doc(orderId).snapshots().listen((snapshot) {
      if (snapshot.exists) {
        final index = _orders.indexWhere((order) => order.id == orderId);
        if (index != -1) {
          // If the order is in the list, update it with the new data.
          _orders[index] = OrderModel.fromMap(snapshot.id, snapshot.data()!);
          notifyListeners();
        }
      }
    }, onError: (error) {
      // Log any errors on the stream.
      print('Error tracking order: $error');
    });
  }

  /// Cleans up resources when the provider is disposed.
  @override
  void dispose() {
    // Cancel the real-time order subscription to prevent memory leaks.
    _orderSubscription?.cancel();
    super.dispose();
  }
}
