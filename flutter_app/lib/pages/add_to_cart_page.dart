import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/cart_provider.dart';
import '../models/item.dart';
import 'checkout_page.dart';

class AddToCartPage extends StatelessWidget {
  const AddToCartPage({Key? key}) : super(key: key);
  static const routeName = '/add_to_cart';

  @override
  Widget build(BuildContext context) {
    final cart = Provider.of<CartProvider>(context);
    final args = ModalRoute.of(context)?.settings.arguments;
    final Item? incomingItem = args is Item ? args : null;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Food Cart'),
        leading: IconButton(icon: const Icon(Icons.arrow_back), onPressed: () => Navigator.maybePop(context)),
      ),
      body: incomingItem != null
          ? _buildAddItemView(context, cart, incomingItem)
          : cart.items.isEmpty
              ? const Center(child: Text('Cart is empty'))
              : Column(
                  children: [
                    Expanded(
                      child: ListView.builder(
                        itemCount: cart.items.length,
                        itemBuilder: (context, index) {
                          final c = cart.items[index];
                          return Card(
                            margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                            child: Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: Row(
                                children: [
                                  c.item.image.isNotEmpty
                                      ? Image.network(c.item.image, width: 64, height: 64, fit: BoxFit.cover, errorBuilder: (cxt, e, s) => const Icon(Icons.fastfood))
                                      : const Icon(Icons.fastfood),
                                  const SizedBox(width: 12),
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text(c.item.name, style: const TextStyle(fontWeight: FontWeight.bold)),
                                        const SizedBox(height: 6),
                                        Text('₱${c.item.price.toStringAsFixed(2)}'),
                                        if (c.addOns.isNotEmpty) ...[
                                          const SizedBox(height: 6),
                                          Text('Add-ons: ${c.addOns.map((a) => a.name).join(', ')}', style: const TextStyle(fontSize: 12)),
                                        ]
                                      ],
                                    ),
                                  ),
                                  Column(
                                    children: [
                                      IconButton(onPressed: () => cart.increaseQuantity(c), icon: const Icon(Icons.add)),
                                      Text('${c.quantity}'),
                                      IconButton(onPressed: () => cart.decreaseQuantity(c), icon: const Icon(Icons.remove)),
                                    ],
                                  ),
                                  Checkbox(
                                    value: c.selected,
                                    onChanged: (v) {
                                      if (v == true) {
                                        cart.selectItem(c);
                                      } else {
                                        c.selected = false; // allow de-select (rare)
                                      }
                                    },
                                  )
                                ],
                              ),
                            ),
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
                          ElevatedButton(
                            onPressed: () {
                              if (cart.items.isEmpty) {
                                ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Your cart is empty')));
                                return;
                              }
                              if (cart.selectedItem == null) {
                                ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Please select an item to checkout')));
                                return;
                              }
                              Navigator.pushNamed(context, CheckoutPage.routeName);
                            },
                            child: const Text('Checkout'),
                          ),
                        ],
                      ),
                    )
                  ],
                ),
    );
  }

  Widget _buildAddItemView(BuildContext context, CartProvider cart, Item item) {
    int qty = 1;
    return StatefulBuilder(builder: (context, setState) {
      return Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            if (item.image.isNotEmpty)
              Image.network(item.image, height: 180, fit: BoxFit.cover, errorBuilder: (c, e, s) => const SizedBox()),
            const SizedBox(height: 12),
            Text(item.name, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Text(item.description),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                IconButton(onPressed: () => setState(() {
                      if (qty > 1) qty--;
                    }), icon: const Icon(Icons.remove)),
                Text('$qty', style: const TextStyle(fontSize: 18)),
                IconButton(onPressed: () => setState(() {
                      qty++;
                    }), icon: const Icon(Icons.add)),
              ],
            ),
            const SizedBox(height: 12),
            ElevatedButton(
              onPressed: () {
                cart.addItemAsCart(item, quantity: qty);
                ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Added to cart')));
                Navigator.pop(context);
              },
              child: const Text('Add to cart'),
            )
          ],
        ),
      );
    });
  }
}
