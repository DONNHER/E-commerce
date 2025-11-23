import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/cart_provider.dart';
import '../models/cart_item.dart';
import 'checkout_page.dart';

class CartPage extends StatelessWidget {
  const CartPage({Key? key}) : super(key: key);
  static const routeName = '/cart';

  @override
  Widget build(BuildContext context) {
    final cart = Provider.of<CartProvider>(context);
    bool allSelected = cart.items.isNotEmpty && cart.items.every((item) => item.isSelected);

    return Scaffold(
      appBar: AppBar(title: const Text('Cart')),
      body: cart.items.isEmpty
          ? const Center(child: Text('Your cart is empty'))
          : Column(
              children: [
                Expanded(
                  child: ListView.builder(
                    itemCount: cart.items.length,
                    itemBuilder: (context, index) {
                      final CartItem c = cart.items[index];
                      return Dismissible(
                        key: Key(c.item.id),
                        direction: DismissDirection.endToStart,
                        onDismissed: (direction) {
                          cart.removeCartItem(c);
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('Item removed from cart')),
                          );
                        },
                        background: Container(
                          color: Colors.red,
                          alignment: Alignment.centerRight,
                          padding: const EdgeInsets.only(right: 20.0),
                          child: const Icon(Icons.delete, color: Colors.white),
                        ),
                        child: ListTile(
                          leading: Checkbox(
                            value: c.isSelected,
                            onChanged: (bool? value) {
                              cart.toggleSelection(c);
                            },
                          ),
                          title: Text(c.item.name),
                          subtitle: Text('₱${c.item.price.toStringAsFixed(2)}'),
                          trailing: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              IconButton(onPressed: () => cart.decreaseQuantity(c), icon: const Icon(Icons.remove)),
                              Text('${c.quantity}'),
                              IconButton(onPressed: () => cart.increaseQuantity(c), icon: const Icon(Icons.add)),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(12.0),
                  child: Column(
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Row(
                            children: [
                              Checkbox(
                                value: allSelected,
                                onChanged: (bool? value) {
                                  cart.selectAll(value ?? false);
                                },
                              ),
                              const Text('Select All'),
                            ],
                          ),
                          Text('Total (${cart.totalItems} items): ₱${cart.total.toStringAsFixed(2)}', style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                        ],
                      ),
                      const SizedBox(height: 10),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: cart.totalItems > 0
                              ? () => Navigator.pushNamed(context, CheckoutPage.routeName)
                              : null,
                          child: const Text('Checkout'),
                        ),
                      ),
                    ],
                  ),
                )
              ],
            ),
    );
  }
}
