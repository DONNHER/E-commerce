import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'add_product_page.dart';

class SellerDashboardPage extends StatefulWidget {
  const SellerDashboardPage({Key? key}) : super(key: key);

  static const routeName = '/seller-dashboard';

  @override
  State<SellerDashboardPage> createState() => _SellerDashboardPageState();
}

class _SellerDashboardPageState extends State<SellerDashboardPage> {
  int _selectedIndex = 0;

  static const List<Widget> _widgetOptions = <Widget>[
    OrdersPage(),
    SellerProductsTab(), // Renamed from ProductsPage
  ];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: _widgetOptions.elementAt(_selectedIndex),
      ),
      bottomNavigationBar: BottomNavigationBar(
        items: const <BottomNavigationBarItem>[
          BottomNavigationBarItem(
            icon: Icon(Icons.list_alt),
            label: 'Orders',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.store),
            label: 'Products',
          ),
        ],
        currentIndex: _selectedIndex,
        selectedItemColor: const Color(0xFF2CB57A),
        onTap: _onItemTapped,
      ),
    );
  }
}

class OrdersPage extends StatefulWidget {
  const OrdersPage({Key? key}) : super(key: key);

  @override
  State<OrdersPage> createState() => _OrdersPageState();
}

class _OrdersPageState extends State<OrdersPage> {
  @override
  Widget build(BuildContext context) {
    final user = FirebaseAuth.instance.currentUser;

    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Orders'),
          bottom: const TabBar(
            tabs: [
              Tab(text: 'Express'),
              Tab(text: 'Standard'),
              Tab(text: 'Scheduled'),
            ],
          ),
        ),
        body: user == null
            ? const Center(child: Text('Please log in to see your orders.'))
            : TabBarView(
                children: [
                  _buildOrdersInnerTab(user.uid, 'Express'),
                  _buildOrdersInnerTab(user.uid, 'Standard'),
                  _buildOrdersInnerTab(user.uid, 'Scheduled'),
                ],
              ),
      ),
    );
  }

  Widget _buildOrdersInnerTab(String sellerId, String orderType) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: const TabBar(
          labelColor: Colors.black,
          tabs: [
            Tab(text: 'NEW'),
            Tab(text: 'ACCEPTED'),
            Tab(text: 'UPCOMING'),
          ],
        ),
        body: TabBarView(
          children: [
            _buildOrderList(sellerId, 'New'),
            _buildOrderList(sellerId, 'Accepted'),
            _buildOrderList(sellerId, 'Upcoming'),
          ],
        ),
      ),
    );
  }

  Widget _buildOrderList(String sellerId, String status) {
    return StreamBuilder<QuerySnapshot>(
      stream: FirebaseFirestore.instance
          .collection('orders')
          .where('items.sellerId', isEqualTo: sellerId) // This query might need adjustment based on your data structure
          .where('status', isEqualTo: status)
          .snapshots(),
      builder: (context, snapshot) {
        if (snapshot.hasError) {
          return Center(child: Text('Error: ${snapshot.error}'));
        }
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }

        final orders = snapshot.data!.docs;

        return ListView.builder(
          itemCount: orders.length,
          itemBuilder: (context, index) {
            final order = orders[index];
            // You might need to adjust how you get the item name and quantity based on your data structure
            final item = order['items'][0];
            return Card(
              child: ListTile(
                leading: const Icon(Icons.receipt),
                title: Text('Order #${order.id}'),
                subtitle: Text('${item['name']} x${item['quantity']}'),
              ),
            );
          },
        );
      },
    );
  }
}

class SellerProductsTab extends StatefulWidget { 
  const SellerProductsTab({Key? key}) : super(key: key);

  @override
  State<SellerProductsTab> createState() => _SellerProductsTabState();
}

class _SellerProductsTabState extends State<SellerProductsTab> {
  @override
  Widget build(BuildContext context) {
    final user = FirebaseAuth.instance.currentUser;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Your Products'),
      ),
      body: user == null
          ? const Center(child: Text('Please log in to see your products.'))
          : StreamBuilder<QuerySnapshot>(
              stream: FirebaseFirestore.instance
                  .collection('items')
                  .where('sellerId', isEqualTo: user.uid)
                  .snapshots(),
              builder: (context, snapshot) {
                if (snapshot.hasError) {
                  return Center(child: Text('Error: ${snapshot.error}'));
                }
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }

                final products = snapshot.data!.docs;

                return ListView.builder(
                  itemCount: products.length,
                  itemBuilder: (context, index) {
                    final product = products[index];
                    return Card(
                      child: ListTile(
                        leading: Image.network(product['image'], width: 50, height: 50, fit: BoxFit.cover),
                        title: Text(product['name']),
                        subtitle: Text(product['description']),
                        trailing: IconButton(
                          icon: const Icon(Icons.edit),
                          onPressed: () {
                            // TODO: Navigate to an edit product page
                          },
                        ),
                      ),
                    );
                  },
                );
              },
            ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.pushNamed(context, AddProductPage.routeName);
        },
        backgroundColor: const Color(0xFF2CB57A),
        child: const Icon(Icons.add),
      ),
    );
  }
}
