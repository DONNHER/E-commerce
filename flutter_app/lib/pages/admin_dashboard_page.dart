import 'package:flutter/material.dart';

class AdminDashboardPage extends StatelessWidget {
  const AdminDashboardPage({Key? key}) : super(key: key);

  static const routeName = '/admin-dashboard';

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 4,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Admin Dashboard'),
          bottom: const TabBar(
            tabs: [
              Tab(text: 'Users'),
              Tab(text: 'Orders'),
              Tab(text: 'Products'),
              Tab(text: 'Adds'),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            _buildUsersTab(),
            _buildOrdersTab(),
            _buildProductsTab(),
            _buildAddsTab(),
          ],
        ),
        bottomNavigationBar: BottomAppBar(
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              IconButton(icon: const Icon(Icons.home), onPressed: () {}),
              IconButton(icon: const Icon(Icons.person), onPressed: () {}),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildUsersTab() {
    return const DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: TabBar(
          labelColor: Colors.black,
          tabs: [
            Tab(text: 'User'),
            Tab(text: 'Seller'),
            Tab(text: 'Admin'),
          ],
        ),
        body: TabBarView(
          children: [
            Center(child: Text('User List')),
            Center(child: Text('Seller List')),
            Center(child: Text('Admin List')),
          ],
        ),
      ),
    );
  }

  Widget _buildOrdersTab() {
    return const DefaultTabController(
      length: 4,
      child: Scaffold(
        appBar: TabBar(
          labelColor: Colors.black,
          tabs: [
            Tab(text: 'All'),
            Tab(text: 'Pending'),
            Tab(text: 'Approved'),
            Tab(text: 'Out of Delivery'),
          ],
        ),
        body: TabBarView(
          children: [
            Center(child: Text('All Orders')),
            Center(child: Text('Pending Orders')),
            Center(child: Text('Approved Orders')),
            Center(child: Text('Out of Delivery Orders')),
          ],
        ),
      ),
    );
  }

  Widget _buildProductsTab() {
    return const Center(child: Text('Product List'));
  }

  Widget _buildAddsTab() {
    return const Center(child: Text('Adds List'));
  }
}
