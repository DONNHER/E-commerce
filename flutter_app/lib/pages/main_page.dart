import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/item.dart';
import '../services/temp_storage.dart';
import '../providers/cart_provider.dart';
import 'products_page.dart';
import 'cart_page.dart';

class MainPage extends StatefulWidget {
  const MainPage({Key? key}) : super(key: key);

  @override
  State<MainPage> createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
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
      backgroundColor: const Color(0xFFE6F5EC),
      body: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Top header: location, avatar, icons
            Padding(
              padding: const EdgeInsets.only(left: 18, right: 18, top: 10, bottom: 2),
              child: Row(
                children: [
                  // Time (simulate)
                  const Text("12:21", style: TextStyle(fontSize: 18)),
                  const SizedBox(width: 16),
                  const Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text("Your Location", style: TextStyle(fontSize: 15)),
                      Row(
                        children: [
                          Icon(Icons.location_on, color: Color(0xFF2CB57A), size: 18),
                          Text("8709 Poblacion", style: TextStyle(fontWeight: FontWeight.bold)),
                          Icon(Icons.expand_more, size: 18),
                        ],
                      )
                    ],
                  ),
                  const Spacer(),
                  // Small icon button (simulate radar)
                  Icon(Icons.gps_fixed, color: Colors.blue[700], size: 32),
                  const SizedBox(width: 14),
                  // Avatar
                  CircleAvatar(
                    radius: 20,
                    backgroundColor: const Color(0xFF2CB57A).withAlpha(102), // Replaced withOpacity(0.4)
                    child: const CircleAvatar(
                      radius: 17,
                      backgroundColor: Colors.white,
                      child: Icon(Icons.person, size: 22, color: Colors.blueGrey),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),

            // NEW USER? headline + big buttons
            const Padding(
              padding: EdgeInsets.symmetric(horizontal: 18.0),
              child: Text("New user?", style: TextStyle(
                fontWeight: FontWeight.bold, fontSize: 22
              )),
            ),
            const SizedBox(height: 12),

            const SizedBox(height: 18),

            // Promo card/banner
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
                  _categoryCard('assets/images/sushi.png', 'Sushi', Colors.black87),
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
            Expanded(
              child: storage.items.isEmpty
                ? const Center(child: CircularProgressIndicator())
                : ListView.builder(
                  padding: const EdgeInsets.only(bottom: 16),
                  itemCount: storage.items.length,
                  itemBuilder: (context, idx) => _productCard(storage.items[idx]),
                ),
            ),
            // Footer navigation
            _footerNav(),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: Colors.yellow,
        onPressed: () => Navigator.pushNamed(context, CartPage.routeName),
        child: const Icon(Icons.shopping_cart, color: Colors.black, size: 32),
      ),
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

  Widget _productCard(Item item) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 10.0, vertical: 8.0),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(18),
        child: Container(
          color: Colors.black,
          child: Stack(
            children: [
              Row(
                children: [
                  Container(
                    width: 110,
                    height: 85,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(16),
                      image: DecorationImage(
                          image: NetworkImage(item.image),
                          fit: BoxFit.cover
                      ),
                    ),
                  ),
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.fromLTRB(12,10,12,8),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            children: [
                              Container(
                                decoration: BoxDecoration(
                                  color: Colors.white70,
                                  borderRadius: BorderRadius.circular(8),
                                ),
                                padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 4),
                                child: Text('₱${item.price}', style: const TextStyle(fontWeight: FontWeight.bold)),
                              ),
                              const Spacer(),
                              const Icon(Icons.favorite, color: Colors.red, size: 26),
                            ],
                          ),
                          const SizedBox(height: 8),
                          Text(item.name, style: const TextStyle(color: Colors.white, fontSize: 17, fontWeight: FontWeight.bold)),
                          const SizedBox(height: 7),
                          Row(
                            children: [
                              Expanded(
                                child: Container(
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    borderRadius: BorderRadius.circular(20)
                                  ),
                                  padding: const EdgeInsets.symmetric(horizontal: 11, vertical: 10),
                                  child: Text(item.name, style: const TextStyle(fontSize: 16)),
                                ),
                              ),
                              const SizedBox(width: 10),
                              ElevatedButton(
                                onPressed: (){
                                  Provider.of<CartProvider>(context, listen: false).addItemAsCart(item);
                                },
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: const Color(0xFF2CB57A),
                                  shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(19),
                                  ),
                                  minimumSize: const Size(90, 34),
                                ),
                                child: const Text("Add to Cart", style: TextStyle(color: Colors.black)),
                              )
                            ],
                          )
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _footerNav() {
    return Container(
      color: Colors.black,
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          IconButton(onPressed: () {}, icon: const Icon(Icons.home, color: Color(0xFF2CB57A), size: 34)),
          IconButton(onPressed: () {}, icon: const Icon(Icons.search, color: Color(0xFF2CB57A), size: 34)),
          IconButton(onPressed: () {}, icon: const Icon(Icons.assignment_turned_in_rounded, color: Color(0xFF2CB57A), size: 34)),
          IconButton(onPressed: () {}, icon: const Icon(Icons.person, color: Color(0xFF2CB57A), size: 34)),
        ],
      ),
    );
  }
}
