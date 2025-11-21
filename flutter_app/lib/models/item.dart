/// This file defines the data models for products (`Item`) and their optional
/// modifications (`AddOn`). These models are used to structure and parse data
/// fetched from the Firestore database.

import 'package:flutter/foundation.dart';

/// Represents an add-on or modification for a product.
/// For example, "Extra Cheese" for a pizza.
class AddOn {
  /// The name of the add-on (e.g., "Extra Cheese").
  String name;
  /// The additional cost for this add-on.
  double price;
  /// The name of the parent item this add-on is associated with.
  String itemName;
  /// A flag to indicate if this add-on has been selected by the user.
  bool isChecked;

  /// Creates an instance of an [AddOn].
  AddOn({required this.name, required this.price, required this.itemName, this.isChecked = false});

  /// Creates an [AddOn] instance from a Firestore data map.
  ///
  /// This factory constructor includes robust parsing with a `try-catch` block
  /// and provides default values to prevent crashes from malformed data.
  factory AddOn.fromMap(Map<String, dynamic> map) {
    try {
      return AddOn(
        // Use null-aware operators to provide default empty strings.
        name: map['addOnName'] as String? ?? '',
        // Safely parse numbers to double, defaulting to 0.0.
        price: (map['addOnPrice'] as num?)?.toDouble() ?? 0.0,
        itemName: map['ItemName'] as String? ?? '',
        // Safely parse booleans, defaulting to false.
        isChecked: map['isChecked'] as bool? ?? false,
      );
    } catch (e) {
      // If parsing fails, log the error and return a default, non-null object.
      debugPrint('Error parsing AddOn: $e');
      return AddOn(name: '', price: 0.0, itemName: '');
    }
  }

  /// Converts this [AddOn] instance into a map for storing in Firestore.
  Map<String, dynamic> toMap() => {
        'addOnName': name,
        'addOnPrice': price,
        'ItemName': itemName,
        'isChecked': isChecked,
      };
}

/// Represents a single product or item available for sale.
class Item {
  /// The unique identifier for the item, typically the Firestore document ID.
  String? id;
  /// The URL for the product's image.
  String image;
  /// The price of the item.
  double price;
  /// The name of the item.
  String name;
  /// The available stock or quantity.
  int quantity;
  /// A detailed description of the item.
  String description;
  /// A list of ingredients in the item.
  String ingredients;
  /// The category the item belongs to (e.g., "Burger", "Pizza").
  String category;
  /// The type of the product (e.g., "Main Course", "Dessert").
  String type;
  /// A flag indicating if the user has marked this item as a favorite.
  bool isFavorite;
  /// The name of the store or restaurant selling the item.
  String storeName;
  /// The current availability status (e.g., "Available", "Sold Out").
  String status;
  /// A list of available add-ons for this item.
  List<AddOn> addOns;

  /// Creates an instance of an [Item].
  Item({
    this.id,
    required this.image,
    required this.price,
    required this.name,
    this.quantity = 0,
    this.description = '',
    this.ingredients = '',
    this.category = '',
    this.type = '',
    this.isFavorite = false,
    this.storeName = 'Calayo Restaurant',
    this.status = 'Pending',
    this.addOns = const [],
  });

  /// Creates an [Item] instance from a Firestore data map.
  ///
  /// This factory is designed for safe data parsing, with a `try-catch` block and
  /// default values for all fields to prevent app crashes due to bad data.
  factory Item.fromMap(Map<String, dynamic> map, {String? id}) {
    try {
      var addons = <AddOn>[];
      // Safely parse the list of add-ons, if it exists.
      if (map['addOns'] is List) {
        addons = (map['addOns'] as List)
            .map((e) => AddOn.fromMap(Map<String, dynamic>.from(e as Map)))
            .toList();
      }

      return Item(
        id: id,
        // Provide default empty string for the image URL.
        image: map['Image'] as String? ?? '',
        // Safely parse price from either 'price' or 'Price' fields.
        price: (map['price'] as num? ?? map['Price'] as num? ?? 0).toDouble(),
        // Safely parse name from either 'name' or 'Name' fields.
        name: map['name'] as String? ?? map['Name'] as String? ?? '',
        // Safely parse quantity to an integer.
        quantity: (map['quantity'] as num?)?.toInt() ?? 0,
        description: map['description'] as String? ?? '',
        ingredients: map['ingredients'] as String? ?? '',
        category: map['category'] as String? ?? '',
        type: map['type'] as String? ?? '',
        isFavorite: map['isFavorite'] as bool? ?? false,
        storeName: map['storeName'] as String? ?? 'Calayo Restaurant',
        status: map['status'] as String? ?? 'Pending',
        addOns: addons,
      );
    } catch (e) {
      // If parsing fails, log the error and return a default 'Error' item.
      debugPrint('Error parsing Item: $e');
      return Item(image: '', price: 0, name: 'Error');
    }
  }

  /// Converts this [Item] instance into a map for storing in Firestore.
  Map<String, dynamic> toMap() => {
        'Image': image,
        'price': price,
        'name': name,
        'quantity': quantity,
        'description': description,
        'ingredients': ingredients,
        'category': category,
        'type': type,
        'isFavorite': isFavorite,
        'storeName': storeName,
        'status': status,
        'addOns': addOns.map((a) => a.toMap()).toList(),
      };
}
