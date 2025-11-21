/// This file defines the `TempStorage` service, which is responsible for managing
/// the product (`Item`) data for the application. It follows the Singleton pattern
/// to ensure a single instance is used throughout the app, and it uses a real-time
/// stream from Firestore to keep the product list always in sync.

import 'dart:async';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';
import '../models/item.dart';

/// A service that manages the product items, keeping them in sync with Firestore.
///
/// This class is a `ChangeNotifier`, which means it can be used with `Provider`
/// to notify widgets when the item data changes.
class TempStorage extends ChangeNotifier {
  // A private constructor to enforce the Singleton pattern.
  TempStorage._privateConstructor();
  // The single, static instance of this service.
  static final TempStorage _instance = TempStorage._privateConstructor();
  // A public getter to access the singleton instance.
  static TempStorage get instance => _instance;

  // An instance of the Firebase Firestore database.
  final FirebaseFirestore _db = FirebaseFirestore.instance;
  // A private list to hold the current product items.
  List<Item> _items = [];
  // A subscription to the Firestore stream, to manage the lifecycle.
  StreamSubscription<QuerySnapshot>? _itemsSubscription;

  /// A public getter to access the list of items.
  List<Item> get items => _items;

  /// Initializes a real-time listener to the 'Items' collection in Firestore.
  ///
  /// This method sets up a stream that automatically updates the local `_items` list
  /// whenever the data changes in the database. It returns a `Future` that
  /// completes when the initial data is loaded.
  Future<void> initializeItemsListener() {
    final completer = Completer<void>();
    // Cancel any previously active subscription to avoid memory leaks.
    _itemsSubscription?.cancel();
    _itemsSubscription = _db.collection('Items').snapshots().listen(
      (snapshot) {
        // Map the Firestore documents to a list of `Item` objects.
        _items = snapshot.docs.map((d) => Item.fromMap(d.data(), id: d.id)).toList();
        print('Firebase data synced. Found \${_items.length} items.');
        // Complete the future if it hasn't been completed already.
        if (!completer.isCompleted) {
          completer.complete();
        }
        // Notify all listening widgets that the data has changed.
        notifyListeners();
      },
      onError: (error) {
        // Handle any errors that occur on the stream.
        print("Error syncing items: $error");
        // Clear the items list to reflect the error state.
        _items = [];
        if (!completer.isCompleted) {
          completer.completeError(error);
        }
        notifyListeners();
      },
    );
    return completer.future;
  }

  /// Searches for items by name in the local list (case-insensitive).
  /// Returns a new list containing only the matching items.
  List<Item> searchItems(String query) {
    // Return an empty list if the query is empty or just whitespace.
    if (query.trim().isEmpty) return [];
    final q = query.toLowerCase();
    return _items.where((it) => it.name.toLowerCase().contains(q)).toList();
  }

  /// Cleans up resources when the `TempStorage` instance is no longer needed.
  /// This is essential to prevent memory leaks.
  @override
  void dispose() {
    // Cancel the Firestore stream subscription.
    _itemsSubscription?.cancel();
    super.dispose();
  }
}
