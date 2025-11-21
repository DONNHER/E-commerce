import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/temp_storage.dart';
import '../models/item.dart';
import 'product_details_page.dart';

class SearchResultsPage extends StatelessWidget {
  const SearchResultsPage({Key? key, required this.searchQuery}) : super(key: key);

  final String searchQuery;

  static const routeName = '/search-results';

  @override
  Widget build(BuildContext context) {
    final storage = Provider.of<TempStorage>(context, listen: false);
    final List<Item> results = storage.searchItems(searchQuery);

    return Scaffold(
      appBar: AppBar(
        title: Text('Search Results for "$searchQuery"'),
      ),
      body: results.isEmpty
          ? const Center(
              child: Text('No results found.'),
            )
          : ListView.builder(
              itemCount: results.length,
              itemBuilder: (context, index) {
                final item = results[index];
                return ListTile(
                  leading: Image.network(
                    item.image,
                    width: 56,
                    height: 56,
                    fit: BoxFit.cover,
                    errorBuilder: (c, e, s) => const Icon(Icons.fastfood),
                  ),
                  title: Text(item.name),
                  subtitle: Text('₱${item.price.toStringAsFixed(2)}'),
                  onTap: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => ProductDetailsPage(item: item),
                    ),
                  ),
                );
              },
            ),
    );
  }
}
