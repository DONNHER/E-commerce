/// This file defines the data model for an `OrderModel`.
/// It is used to structure, parse, and manage order data throughout the application,
/// especially for interactions with the Firestore database.

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/foundation.dart';

/// Represents a customer's order.
class OrderModel {
  /// The unique identifier for the order, from the Firestore document ID.
  final String id;
  /// The ID of the user who placed the order.
  final String userId;
  /// The total cost of the order.
  final double total;
  /// A list of items included in the order, stored as a map for each item.
  final List<Map<String, dynamic>> items;
  /// The date and time when the order was created.
  final DateTime createdAt;
  /// The current status of the order (e.g., 'Pending', 'Cancelled', 'Delivered').
  String status;
  /// The geographical location of the order, used for tracking purposes.
  GeoPoint? location;

  /// Creates an instance of an [OrderModel].
  OrderModel({
    required this.id,
    required this.userId,
    required this.total,
    required this.items,
    required this.createdAt,
    this.status = 'Pending',
    this.location,
  });

  /// Converts this [OrderModel] instance into a map for storing in Firestore.
  Map<String, dynamic> toMap() => {
        'userId': userId,
        'total': total,
        'items': items,
        // Convert DateTime to a string format that Firestore can handle.
        'createdAt': createdAt.toIso8601String(),
        'status': status,
        'location': location,
      };

  /// Creates an [OrderModel] instance from a Firestore data map.
  ///
  /// This factory is designed for safe data parsing, with a `try-catch` block
  /// and default values to prevent app crashes from malformed data.
  factory OrderModel.fromMap(String id, Map<String, dynamic> map) {
    try {
      return OrderModel(
        id: id,
        // Use null-aware operators to provide a default empty string.
        userId: map['userId'] as String? ?? '',
        // Safely parse the total as a double.
        total: (map['total'] as num?)?.toDouble() ?? 0.0,
        // Safely parse the list of items.
        items: List<Map<String, dynamic>>.from(map['items'] as List? ?? []),
        // Safely parse the creation date, with a fallback to the current time.
        createdAt: DateTime.tryParse(map['createdAt'] as String? ?? '') ?? DateTime.now(),
        // Default the status to 'Pending' if not provided.
        status: map['status'] as String? ?? 'Pending',
        // Safely parse the location as a GeoPoint.
        location: map['location'] as GeoPoint?,
      );
    } catch (e) {
      // If parsing fails, log the error and return a default, non-null object.
      debugPrint('Error parsing OrderModel: $e');
      return OrderModel(id: '', userId: '', total: 0, items: [], createdAt: DateTime.now());
    }
  }
}
