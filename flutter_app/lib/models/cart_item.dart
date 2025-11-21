import 'item.dart';

class CartItem {
  final Item item;
  int quantity;
  bool selected;
  List<AddOn> addOns;

  CartItem({required this.item, this.quantity = 1, this.selected = false, List<AddOn>? addOns}) : addOns = addOns ?? [];

  double get subtotal => item.price * (quantity <= 0 ? 1 : quantity) + addOns.fold(0.0, (p, a) => p + a.price);
}
