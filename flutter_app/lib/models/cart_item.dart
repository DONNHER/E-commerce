import './item.dart';

class CartItem {
  final Item item;
  int quantity;
  final List<AddOn> addOns;
  bool isSelected; // Tracks if the item is selected for checkout

  CartItem({
    required this.item,
    this.quantity = 1,
    List<AddOn>? addOns,
    this.isSelected = true, // Items are selected by default when added
  }) : addOns = addOns ?? [];

  double get subtotal => item.price * quantity;
}
