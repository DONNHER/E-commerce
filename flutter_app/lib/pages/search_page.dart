import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/temp_storage.dart';
import '../widgets/product_item.dart';

class SearchPage extends StatelessWidget {
  const SearchPage({Key? key}) : super(key: key);
  static const routeName = '/search';

  @override
  Widget build(BuildContext context) {
    final args = ModalRoute.of(context)?.settings.arguments;
    final query = args is String ? args : '';

    final storage = Provider.of<TempStorage>(context);
    final results = storage.searchItems(query);

    return Scaffold(
      appBar: AppBar(title: Text('Search: $query')),
      body: results.isEmpty
          ? const Center(child: Text('No results'))
          : ListView.builder(
              padding: const EdgeInsets.all(12.0),
              itemCount: results.length,
              itemBuilder: (context, index) => ProductItem(item: results[index]),
            ),
    );
  }
}
