/// This file defines the `UserProvider`, which manages the application's
/// current user state. It handles fetching user data from Firestore and signing
/// the user out. It uses the `ChangeNotifier` pattern to update the UI when the
/// user's state changes.

import 'package:flutter/foundation.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/user.dart';

/// Manages the current user's data and authentication state.
class UserProvider extends ChangeNotifier {
  // A private property to hold the current user's data model.
  UserModel? _user;
  /// A public getter to access the current user's data.
  UserModel? get user => _user;

  /// Fetches the current user's data from Firestore and updates the state.
  ///
  /// This method gets the currently signed-in user from Firebase Auth, then uses
  /// the user's UID to fetch the corresponding document from the 'users' collection
  /// in Firestore.
  Future<void> fetchUser() async {
    try {
      final firebaseUser = FirebaseAuth.instance.currentUser;
      if (firebaseUser != null) {
        final doc = await FirebaseFirestore.instance.collection('users').doc(firebaseUser.uid).get();
        if (doc.exists) {
          // If the document exists, parse it into a `UserModel`.
          _user = UserModel.fromFirestore(doc);
          // Notify listening widgets that the user data is available.
          notifyListeners();
        }
      }
    } catch (e) {
      // Log any errors that occur during the process.
      print('Error fetching user: $e');
    }
  }

  /// Signs the current user out of the application.
  Future<void> logout() async {
    try {
      // Sign the user out from Firebase Authentication.
      await FirebaseAuth.instance.signOut();
      // Clear the local user data.
      _user = null;
      // Notify listening widgets that the user has signed out.
      notifyListeners();
    } catch (e) {
      // Log any errors that occur during sign-out.
      print('Error logging out: $e');
    }
  }
}
