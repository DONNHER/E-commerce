/// This file defines the `CartProvider` which is responsible for managing the state
/// of the user's shopping cart. It uses the `ChangeNotifier` pattern to allow widgets
/// to listen for and react to changes in the cart.

import 'package:flutter/foundation.dart';
import '../models/item.dart';
import '../models/cart_item.dart';

/// Manages the state of the shopping cart.
class CartProvider extends ChangeNotifier {
  // A private list to store the items in the cart.
  final List<CartItem> _items = [];

  /// A public, unmodifiable view of the items in the cart.
  List<CartItem> get items => List.unmodifiable(_items);

  /// Returns the currently selected item in the cart, if any.
  CartItem? get selectedItem {
    final idx = _items.indexWhere((c) => c.selected);
    return idx == -1 ? null : _items[idx];
  }

  /// Adds a product to the cart or updates its quantity if it already exists.
  ///
  /// - [item]: The product `Item` to add.
  /// - [quantity]: The number of items to add.
  /// - [addOns]: An optional list of add-ons for the item.
  void addItemAsCart(Item item, {int quantity = 1, List<AddOn>? addOns}) {
    // Check if the item is already in the cart by its ID.
    final idx = _items.indexWhere((c) => c.item.id == item.id);
    if (idx != -1) {
      // If the item exists, increase its quantity.
      final existing = _items[idx];
      existing.quantity += quantity;
      // Add any new add-ons.
      if (addOns != null && addOns.isNotEmpty) existing.addOns.addAll(addOns);
    } else {
      // If the item is not in the cart, add it as a new `CartItem`.
      _items.add(CartItem(item: item, quantity: quantity, addOns: addOns));
    }
    // Notify listening widgets to rebuild.
    notifyListeners();
  }

  /// Removes a specific `CartItem` from the cart.
  void removeCartItem(CartItem cartItem) {
    _items.remove(cartItem);
    notifyListeners();
  }

  /// Removes all items from the cart.
  void clear() {
    _items.clear();
    notifyListeners();
  }

  /// Increases the quantity of a specific `CartItem` by one.
  void increaseQuantity(CartItem cartItem) {
    cartItem.quantity++;
    notifyListeners();
  }

  /// Decreases the quantity of a specific `CartItem` by one, if the quantity is greater than 1.
  void decreaseQuantity(CartItem cartItem) {
    if (cartItem.quantity > 1) {
      cartItem.quantity--;
      notifyListeners();
    }
  }

  /// Marks a specific `CartItem` as selected and deselects all others.
  void selectItem(CartItem cartItem) {
    for (var c in _items) {
      c.selected = false;
    }
    cartItem.selected = true;
    notifyListeners();
  }

  /// Calculates and returns the total price of all items in the cart.
  double get total => _items.fold(0.0, (prev, c) => prev + c.subtotal);
}
