
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/item.dart';
import '../services/temp_storage.dart';
import '../providers/cart_provider.dart';
import 'products_page.dart';
import 'cart_page.dart';
import 'product_details_page.dart';

class UserDashboardPage extends StatefulWidget {
  const UserDashboardPage({Key? key}) : super(key: key);

  static const routeName = '/user-dashboard';

  @override
  State<UserDashboardPage> createState() => _UserDashboardPageState();
}

class _UserDashboardPageState extends State<UserDashboardPage> {
  final TextEditingController _searchController = TextEditingController();
  List<Item> _searchResults = [];
  Timer? _debounce;
  final FocusNode _searchFocusNode = FocusNode();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<TempStorage>(context, listen: false).initializeItemsListener();
    });

    _searchController.addListener(_onSearchChanged);
    _searchFocusNode.addListener(() {
      if (!_searchFocusNode.hasFocus && mounted) {
        setState(() {
          _searchResults = [];
        });
      }
    });
  }

  @override
  void dispose() {
    _searchController.removeListener(_onSearchChanged);
    _searchController.dispose();
    _searchFocusNode.dispose();
    _debounce?.cancel();
    super.dispose();
  }

  void _onSearchChanged() {
    if (_debounce?.isActive ?? false) _debounce!.cancel();
    _debounce = Timer(const Duration(milliseconds: 500), () {
      if (!mounted) return;

      final storage = Provider.of<TempStorage>(context, listen: false);
      final query = _searchController.text.toLowerCase();

      if (query.isEmpty) {
        setState(() {
          _searchResults = [];
        });
        return;
      }

      final results = storage.items.where((item) {
        return item.name.toLowerCase().contains(query);
      }).toList();

      setState(() {
        _searchResults = results;
      });
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
            const Padding(
              padding: EdgeInsets.all(5.0),
              child: Row(
                children: [
                  Icon(Icons.location_on, size: 30),
                  Text("8720 Musuan", style: TextStyle(fontSize: 20, color: Colors.black)),
                  Icon(Icons.arrow_drop_down, size: 20),
                  Spacer(),
                  Icon(Icons.location_on_outlined, size: 50),
                  SizedBox(width: 20),
                  Icon(Icons.person, size: 50),
                ],
              ),
            ),
            
            Padding(
              padding: const EdgeInsets.all(10.0),
              child: TextField(
                focusNode: _searchFocusNode,
                controller: _searchController,
                decoration: const InputDecoration(
                  hintText: 'Search for products...',
                  prefixIcon: Icon(Icons.search),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.all(Radius.circular(5.0)),
                  ),
                  filled: true,
                  fillColor: Colors.white,
                ),
                maxLines: 1,
              ),
            ),

            _buildSearchResults(),

            Expanded(
              child: SingleChildScrollView(
                child: Column(
                  children: [
                    const PromoCarousel(),
                    const SizedBox(height: 4),

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

                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 6.0),
                      child: Row(
                        children: [
                          const Expanded(
                            child: Text('Popular Food', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20)),
                          ),
                          TextButton(
                            onPressed: () => Navigator.pushNamed(context, ProductsPage.routeName),
                            child: const Text('See All', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                          )
                        ],
                      ),
                    ),
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
            const SizedBox(width: 40),
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

  Widget _buildSearchResults() {
    if (_searchController.text.isEmpty || !_searchFocusNode.hasFocus) {
      return const SizedBox.shrink();
    }

    if (_searchResults.isEmpty && _searchController.text.isNotEmpty) {
      return const Padding(
        padding: EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
        child: Text('No products found.'),
      );
    }

    return Container(
      constraints: const BoxConstraints(maxHeight: 220),
      margin: const EdgeInsets.symmetric(horizontal: 10),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(5),
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withOpacity(0.5),
            spreadRadius: 1,
            blurRadius: 3,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: ListView.builder(
        shrinkWrap: true,
        itemCount: _searchResults.length,
        itemBuilder: (context, index) {
          final item = _searchResults[index];
          return ListTile(
            leading: ClipRRect(
              borderRadius: BorderRadius.circular(8.0),
              child: Image.network(
                item.image,
                width: 50,
                height: 50,
                fit: BoxFit.cover,
                errorBuilder: (c, e, s) => const Icon(Icons.fastfood, size: 40),
              ),
            ),
            title: Text(item.name),
            onTap: () {
              FocusScope.of(context).unfocus();
              _searchController.clear();
              setState(() {
                _searchResults = [];
              });
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => ProductDetailsPage(item: item),
                ),
              );
            },
          );
        },
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
        Text(label, style: const TextStyle(fontWeight: FontWeight.w500, fontSize: 15, color: Color(0xFF2CB57A))),
      ],
    );
  }

  Widget _productCard(BuildContext context, Item item) {
    return GestureDetector(
      onTap: () => Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => ProductDetailsPage(item: item),
        ),
      ),
      child: Container(
        height: 200,
        margin: const EdgeInsets.only(bottom: 15, left: 12, right: 12),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(15),
          image: DecorationImage(
            image: NetworkImage(item.image),
            fit: BoxFit.cover,
            onError: (e, s) => print('Error loading image: $e'),
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
                  color: Colors.white.withAlpha(204),
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
              child: Icon(Icons.favorite_border, color: Colors.red, size: 33),
            ),
            Positioned(
              bottom: 0,
              left: 0,
              right: 0,
              child: Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: Colors.black.withAlpha(128),
                  borderRadius: const BorderRadius.only(
                    bottomLeft: Radius.circular(15),
                    bottomRight: Radius.circular(15),
                  )
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(item.name, style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                    GestureDetector(
                      onTap: () {
                        Provider.of<CartProvider>(context, listen: false).addItemAsCart(item);
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('Added to cart!'), duration: Duration(seconds: 1)),
                        );
                      },
                      child: Container(
                        padding: const EdgeInsets.all(6),
                        color: const Color(0xFF2CB57A),
                        child: const Text("Add to Cart", style: TextStyle(color: Colors.black)),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class PromoCarousel extends StatefulWidget {
  const PromoCarousel({Key? key}) : super(key: key);

  @override
  State<PromoCarousel> createState() => _PromoCarouselState();
}

class _PromoCarouselState extends State<PromoCarousel> {
  final List<Map<String, String>> _promoData = [
    {
      'image': 'assets/images/burger.png',
      'title': 'Get 40% off\nYour First Order!',
      'code': 'FIRST40',
    },
    {
      'image': 'assets/images/pizza.png',
      'title': 'Free Delivery\non All Pizza!',
      'code': 'FREEPIZZA',
    },
    {
      'image': 'assets/images/salad.png',
      'title': 'Healthy Bowls\n20% Discount!',
      'code': 'HEALTHY20',
    },
    {
      'image': 'assets/images/burger.png',
      'title': 'Buy One Get One\nOn all Burgers!',
      'code': 'BOGO',
    },
    {
      'image': 'assets/images/pizza.png', 
      'title': 'Mega Pizza Deal\nSave up to 50%!',
      'code': 'MEGADEAL',
    }
  ];

  late final PageController _pageController;
  late final Timer _timer;
  int _currentPage = 0;
  static const int _initialPage = 10000; // A large number to start in the middle

  @override
  void initState() {
    super.initState();
    _pageController = PageController(initialPage: _initialPage);

    _timer = Timer.periodic(const Duration(seconds: 3), (timer) {
      if (_pageController.hasClients) {
        _pageController.nextPage(
          duration: const Duration(milliseconds: 400),
          curve: Curves.easeIn,
        );
      }
    });
  }

  @override
  void dispose() {
    _timer.cancel();
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          height: 128,
          child: PageView.builder(
            controller: _pageController,
            onPageChanged: (int page) {
              setState(() {
                _currentPage = page % _promoData.length;
              });
            },
            itemBuilder: (context, index) {
              final promo = _promoData[index % _promoData.length];
              return Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16.0),
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(22),
                  child: Container(
                    color: const Color(0xFF92E3A9),
                    child: Stack(
                      children: [
                        Positioned(
                          right: 12,
                          top: 18,
                          child: Image.asset(
                            promo['image']!,
                            width: 110,
                            height: 80,
                            fit: BoxFit.cover,
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
                                    child: Text(promo['code']!,
                                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 10),
                              Text(promo['title']!,
                                style: const TextStyle(fontSize: 21, fontWeight: FontWeight.bold),
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
              );
            },
          ),
        ),
        const SizedBox(height: 8),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: List.generate(_promoData.length, (index) {
            return Container(
              width: 8.0,
              height: 8.0,
              margin: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 2.0),
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: _currentPage == index
                    ? const Color.fromRGBO(0, 0, 0, 0.9)
                    : const Color.fromRGBO(0, 0, 0, 0.4),
              ),
            );
          }),
        ),
      ],
    );
  }
}
