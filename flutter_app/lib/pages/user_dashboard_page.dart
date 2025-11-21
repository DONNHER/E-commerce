import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/item.dart';
import '../services/temp_storage.dart';
import '../providers/cart_provider.dart';
import 'products_page.dart';
import 'cart_page.dart';

class UserDashboardPage extends StatefulWidget {
  const UserDashboardPage({Key? key}) : super(key: key);

  static const routeName = '/user-dashboard';

  @override
  State<UserDashboardPage> createState() => _UserDashboardPageState();
}

class _UserDashboardPageState extends State<UserDashboardPage> {
  final TextEditingController _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<TempStorage>(context, listen: false).initializeItemsListener();
    });
  }

  @override
  Widget build(BuildContext context) {
    final storage = Provider.of<TempStorage>(context);

    return Scaffold(
      backgroundColor: const Color(0xFFE6F4F1),
      body: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header from user_d_board.xml
            const Padding(
              padding: EdgeInsets.all(5.0),
              child: Row(
                children: [
                  Icon(Icons.location_on, size: 30), // Approximating @drawable/mark
                  Text("8720 Musuan", style: TextStyle(fontSize: 20, color: Colors.black)),
                  Icon(Icons.arrow_drop_down, size: 20),
                  Spacer(),
                  Icon(Icons.location_on_outlined, size: 50), // Approximating @drawable/locate
                  SizedBox(width: 20),
                  Icon(Icons.person, size: 50), // Approximating @drawable/regis
                ],
              ),
            ),
            
            // Search Bar from Android XML
            Padding(
              padding: const EdgeInsets.all(10.0),
              child: Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: _searchController,
                      decoration: const InputDecoration(
                        hintText: 'Search',
                        prefixIcon: Icon(Icons.search),
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.all(Radius.circular(5.0)),
                        ),
                        filled: true,
                        fillColor: Colors.white,
                      ),
                      maxLines: 1, // Replaced singleLine with maxLines
                    ),
                  ),
                  const SizedBox(width: 20),
                  ElevatedButton(
                      onPressed: () {
                        // TODO: Implement search functionality
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF2CB57A), // Approximating @drawable/green
                        foregroundColor: Colors.white,
                      ),
                      child: const Text("Submit"),
                  ),
                ],
              ),
            ),

            // The rest of the content needs to be in a scroll view
            Expanded(
              child: SingleChildScrollView(
                child: Column(
                  children: [
                    // Promo card/banner -> Corresponds to Adds_Recycler
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16.0),
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(22),
                        child: Container(
                          color: const Color(0xFF92E3A9),
                          height: 128,
                          width: double.infinity,
                          child: Stack(
                            children: [
                              Positioned(
                                right: 12, top: 18,
                                child: Image.asset('assets/images/burger.png',
                                  width: 110, height: 80, fit: BoxFit.cover,
                                  errorBuilder: (c, e, s) => const Icon(Icons.fastfood, size: 72),
                                ),
                              ),
                              Padding(
                                padding: const EdgeInsets.only(left: 16, top: 18, right: 130),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Row(
                                      children: [
                                        const Text("Use code ", style: TextStyle(fontSize: 15)),
                                        Container(
                                          decoration: BoxDecoration(
                                            border: Border.all(color: Colors.black),
                                            borderRadius: BorderRadius.circular(8),
                                            color: Colors.white,
                                          ),
                                          padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 1),
                                          child: const Text("FIRST40",
                                            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                                          ),
                                        ),
                                        const Text(" at Checkout", style: TextStyle(fontSize: 15)),
                                      ],
                                    ),
                                    const SizedBox(height: 7),
                                    const Text("Hurry, offer end soon!", style: TextStyle(fontSize: 13)),
                                    const SizedBox(height: 10),
                                    const Text("Get 40% off\nYour First Order!",
                                      style: TextStyle(
                                          fontSize: 21, fontWeight: FontWeight.bold),
                                    ),
                                    const SizedBox(height: 6),
                                    ElevatedButton(
                                      style: ElevatedButton.styleFrom(
                                        backgroundColor: Colors.black,
                                        minimumSize: const Size(90, 30),
                                        shape: RoundedRectangleBorder(
                                          borderRadius: BorderRadius.circular(16),
                                        ),
                                      ),
                                      onPressed: () {},
                                      child: const Text("ORDER NOW",
                                        style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 14),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(height: 4),

                    // Categories row
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 15.0),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          _categoryCard('assets/images/burger.png', 'Burger', Colors.yellow),
                          _categoryCard('assets/images/pizza.png', 'Pizza', Colors.yellow),
                          _categoryCard('assets/images/salad.png', 'Salad', Colors.black87),
                        ],
                      ),
                    ),

                    // Popular header row
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 6.0),
                      child: Row(
                        children: [
                          const Expanded(
                            child: Text('Popular Food',
                              style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
                            ),
                          ),
                          TextButton(
                            onPressed: () => Navigator.pushNamed(context, ProductsPage.routeName),
                            child: const Text('See All', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                          )
                        ],
                      ),
                    ),
                    // Products list
                    storage.items.isEmpty
                        ? const Center(child: CircularProgressIndicator())
                        : ListView.builder(
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          padding: const EdgeInsets.only(bottom: 16),
                          itemCount: storage.items.length,
                          itemBuilder: (context, idx) => _productCard(context, storage.items[idx]),
                        ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
      bottomNavigationBar: BottomAppBar(
        color: Colors.black,
        shape: const CircularNotchedRectangle(),
        notchMargin: 8.0,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            IconButton(onPressed: () {}, icon: const Icon(Icons.home, color: Color(0xFF2CB57A), size: 34)),
            IconButton(onPressed: () {}, icon: const Icon(Icons.fastfood, color: Colors.white, size: 34)),
            const SizedBox(width: 40), // The space for the FAB
            IconButton(onPressed: () {}, icon: const Icon(Icons.history, color: Colors.white, size: 34)),
            IconButton(onPressed: () {}, icon: const Icon(Icons.person, color: Colors.white, size: 34)),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: Colors.yellow,
        onPressed: () => Navigator.pushNamed(context, CartPage.routeName),
        child: const Icon(Icons.shopping_cart, color: Colors.black, size: 32),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
    );
  }

  Widget _categoryCard(String icon, String label, Color bg) {
    return Column(
      children: [
        Container(
          width: 56, height: 56,
          decoration: BoxDecoration(
            color: bg, borderRadius: BorderRadius.circular(13),
          ),
          child: Image.asset(icon, fit: BoxFit.contain,
            errorBuilder: (c, e, s) => const Icon(Icons.fastfood, size: 40),
          ),
        ),
        const SizedBox(height: 4),
        Text(label, style: const TextStyle(fontWeight: FontWeight.w500,
            fontSize: 15, color: Color(0xFF2CB57A))),
      ],
    );
  }

  Widget _productCard(BuildContext context, Item item) {
    return Container(
      height: 200,
      margin: const EdgeInsets.only(bottom: 15, left: 12, right: 12),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        image: DecorationImage(
          image: NetworkImage(item.image),
          fit: BoxFit.cover,
        ),
      ),
      child: Stack(
        children: [
          Positioned(
            top: 0,
            left: 0,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 5),
              decoration: BoxDecoration(
                color: Colors.white.withAlpha(204), // Replaced withOpacity(0.8)
                borderRadius: BorderRadius.circular(5),
              ),
              child: Row(
                children: [
                  const Text('₱', style: TextStyle(color: Colors.black)),
                  Text(item.price.toString(), style: const TextStyle(color: Colors.black)),
                ],
              ),
            ),
          ),
          const Positioned(
            top: 0,
            right: 0,
            child: Icon(Icons.favorite_border, color: Colors.red, size: 33), // Approximating @drawable/fav
          ),
          Positioned(
            bottom: 0,
            left: 0,
            right: 0,
            child: Container(
              padding: const EdgeInsets.all(10),
              color: Colors.black.withAlpha(128), // Replaced withOpacity(0.5)
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(item.name, style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                  GestureDetector(
                    onTap: () => Provider.of<CartProvider>(context, listen: false).addItemAsCart(item),
                    child: Container(
                      padding: const EdgeInsets.all(6),
                      color: const Color(0xFF2CB57A), // Approximating @drawable/green
                      child: const Text("Add to Cart", style: TextStyle(color: Colors.black)),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
