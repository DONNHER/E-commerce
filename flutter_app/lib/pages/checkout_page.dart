import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/cart_provider.dart';
import '../providers/user_provider.dart';
import '../models/order.dart';
import 'order_success_page.dart';

class CheckoutPage extends StatelessWidget {
  const CheckoutPage({Key? key}) : super(key: key);
  static const routeName = '/checkout';

  Future<void> _placeOrder(BuildContext context) async {
    final cart = Provider.of<CartProvider>(context, listen: false);
    if (cart.items.isEmpty) return;

    final user = FirebaseAuth.instance.currentUser;
    if (user == null) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Please login to checkout')));
      return;
    }

    final items = cart.items
        .map((c) => {
              'id': c.item.id,
              'name': c.item.name,
              'price': c.item.price,
              'quantity': c.quantity,
              'addOns': c.addOns.map((a) => a.toMap()).toList(),
            })
        .toList();

    final doc = FirebaseFirestore.instance.collection('orders').doc();
    final order = OrderModel(id: doc.id, userId: user.uid, total: cart.total, items: items, createdAt: DateTime.now());

    await doc.set(order.toMap());
    cart.clear();
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Order placed')));
    Navigator.of(context).pushReplacementNamed(OrderSuccessPage.routeName);
  }

  @override
  Widget build(BuildContext context) {
    final cart = Provider.of<CartProvider>(context);
    final user = Provider.of<UserProvider>(context).user;

    return Scaffold(
      backgroundColor: const Color(0xFFE6F4F1),
      appBar: AppBar(
        backgroundColor: const Color(0xFFE6F4F1),
        title: const Text('Checkout'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Address
            Row(
              children: [
                const Icon(Icons.home, color: Colors.green),
                const SizedBox(width: 10),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Home', style: TextStyle(fontWeight: FontWeight.bold)),
                    Text(user?.name ?? 'No address found'),
                  ],
                )
              ],
            ),
            const SizedBox(height: 10),
            // Order Total
            Text('Order total (${cart.items.length} item${cart.items.length > 1 ? 's' : ''}):'),
            Text(cart.items.map((e) => e.item.name).join(', '), style: const TextStyle(fontSize: 25, fontWeight: FontWeight.bold)),
            const Text('₱0 Delivery fee over ₱30', style: TextStyle(color: Colors.green)),
            const Divider(thickness: 2, color: Colors.black),
            // Payment Option
            const Text('Payment Option', style: TextStyle(fontWeight: FontWeight.bold)),
            Row(
              children: [
                Checkbox(value: true, onChanged: (val) {}), // Placeholder
                const Text('Cash on Delivery'),
              ],
            ),
            const Text('Other payment option in the future.'),
            const Divider(thickness: 2, color: Colors.black),
            // Order Summary
            const Text('Order Summary', style: TextStyle(fontWeight: FontWeight.bold)),
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: cart.items.length,
              itemBuilder: (context, index) {
                final item = cart.items[index];
                return Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text('${item.item.name} x${item.quantity}'),
                    Text('₱${(item.item.price * item.quantity).toStringAsFixed(2)}'),
                  ],
                );
              },
            ),
            const Divider(),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('Total:', style: TextStyle(fontWeight: FontWeight.bold)),
                Text('₱${cart.total.toStringAsFixed(2)}', style: const TextStyle(fontWeight: FontWeight.bold)),
              ],
            ),
          ],
        ),
      ),
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const TextField(
              decoration: InputDecoration(
                hintText: 'Write notes',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 10),
            ElevatedButton(
              onPressed: () => _placeOrder(context),
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.green,
                minimumSize: const Size(double.infinity, 50),
              ),
              child: const Text('Checkout'),
            ),
          ],
        ),
      ),
    );
  }
}
