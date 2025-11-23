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

  /// Adds a product to the cart or updates its quantity if it already exists.
  void addItemAsCart(Item item, {int quantity = 1, List<AddOn>? addOns}) {
    final idx = _items.indexWhere((c) => c.item.id == item.id);
    if (idx != -1) {
      final existing = _items[idx];
      existing.quantity += quantity;
      if (addOns != null && addOns.isNotEmpty) existing.addOns.addAll(addOns);
    } else {
      _items.add(CartItem(item: item, quantity: quantity, addOns: addOns));
    }
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

  /// Toggles the selection of a specific `CartItem`.
  void toggleSelection(CartItem cartItem) {
    cartItem.isSelected = !cartItem.isSelected;
    notifyListeners();
  }

  /// Selects or deselects all items in the cart.
  void selectAll(bool isSelected) {
    for (var c in _items) {
      c.isSelected = isSelected;
    }
    notifyListeners();
  }

  /// Calculates and returns the total price of all selected items in the cart.
  double get total => _items.where((c) => c.isSelected).fold(0.0, (prev, c) => prev + c.subtotal);

  /// Calculates and returns the total number of all selected items in the cart.
  int get totalItems => _items.where((c) => c.isSelected).fold(0, (prev, c) => prev + c.quantity);
}
