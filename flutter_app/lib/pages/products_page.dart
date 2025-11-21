import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/temp_storage.dart';
import '../widgets/product_item.dart';

class ProductsPage extends StatelessWidget {
  const ProductsPage({Key? key}) : super(key: key);
  static const routeName = '/products';

  @override
  Widget build(BuildContext context) {
    final storage = Provider.of<TempStorage>(context);
    final items = storage.items;

    return Scaffold(
      appBar: AppBar(title: const Text('Products')),
      body: items.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : GridView.builder(
              padding: const EdgeInsets.all(12.0),
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                childAspectRatio: 0.78,
                mainAxisSpacing: 12,
                crossAxisSpacing: 12,
              ),
              itemCount: items.length,
              itemBuilder: (context, index) => ProductItem(item: items[index]),
            ),
    );
  }
}
