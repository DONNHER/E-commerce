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
                      return ListTile(
                        leading: c.item.image.isNotEmpty ? Image.network(c.item.image, width: 56, height: 56, fit: BoxFit.cover, errorBuilder: (c,e,s)=>const Icon(Icons.fastfood)) : const Icon(Icons.fastfood),
                        title: Text(c.item.name),
                        subtitle: Text('₱${c.item.price.toStringAsFixed(2)} x ${c.quantity}'),
                        trailing: IconButton(onPressed: () => cart.removeCartItem(c), icon: const Icon(Icons.delete)),
                      );
                    },
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(12.0),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('Total: ₱${cart.total.toStringAsFixed(2)}', style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                      ElevatedButton(onPressed: () => Navigator.pushNamed(context, CheckoutPage.routeName), child: const Text('Checkout'))
                    ],
                  ),
                )
              ],
            ),
    );
  }
}
