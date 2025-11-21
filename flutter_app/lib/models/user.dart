/// This file defines the data model for a `UserModel`.
/// It is used to structure, parse, and manage user data throughout the application,
/// especially for interactions with Firebase Authentication and Firestore.

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/foundation.dart';

/// Represents a user of the application.
class UserModel {
  /// The unique identifier for the user, from Firebase Authentication.
  final String uid;
  /// The user's email address.
  final String email;
  /// The user's display name.
  final String name;
  /// The user's role, which determines their permissions (e.g., 'user', 'admin').
  final String role;
  /// The Firebase Cloud Messaging (FCM) token for sending push notifications to the user's device.
  final String? fcmToken;

  /// Creates an instance of a [UserModel].
  UserModel({
    required this.uid,
    required this.email,
    required this.name,
    required this.role,
    this.fcmToken,
  });

  /// Creates a [UserModel] instance from a Firestore document.
  ///
  /// This factory is designed for safe data parsing, with a `try-catch` block
  /// and default values to prevent app crashes from malformed data.
  factory UserModel.fromFirestore(DocumentSnapshot doc) {
    try {
      final data = doc.data() as Map<String, dynamic>;
      return UserModel(
        uid: doc.id,
        // Use null-aware operators to provide default empty strings.
        email: data['email'] as String? ?? '',
        name: data['name'] as String? ?? '',
        // Default the role to 'user' if not specified.
        role: data['role'] as String? ?? 'user',
        // The FCM token can be null.
        fcmToken: data['fcmToken'] as String?,
      );
    } catch (e) {
      // If parsing fails, log the error and return a default, non-null object.
      debugPrint('Error parsing UserModel: $e');
      return UserModel(uid: '', email: '', name: '', role: 'user');
    }
  }

  /// Converts this [UserModel] instance into a map for storing in Firestore.
  Map<String, dynamic> toMap() {
    return {
      'email': email,
      'name': name,
      'role': role,
      'fcmToken': fcmToken,
    };
  }
}
