import 'package:flutter/material.dart';
import '../models/item.dart';

class ProductItem extends StatelessWidget {
  final Item item;
  const ProductItem({Key? key, required this.item}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 6.0),
      child: ListTile(
        leading: item.image.isNotEmpty
            ? Image.network(item.image, width: 64, height: 64, fit: BoxFit.cover, errorBuilder: (c,e,s)=>const Icon(Icons.fastfood))
            : const Icon(Icons.fastfood),
        title: Text(item.name),
        subtitle: Text(item.description),
        trailing: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('₱ ${item.price.toStringAsFixed(2)}', style: const TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 6),
            ElevatedButton(onPressed: () {
              Navigator.pushNamed(context, '/add_to_cart', arguments: item);
            }, child: const Text('Add'))
          ],
        ),
      ),
    );
  }
}
