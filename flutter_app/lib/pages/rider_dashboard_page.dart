import 'package:flutter/material.dart';

class RiderDashboardPage extends StatelessWidget {
  const RiderDashboardPage({Key? key}) : super(key: key);

  static const routeName = '/rider-dashboard';

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Dashboard'),
          bottom: const TabBar(
            tabs: [
              Tab(text: 'Orders to Deliver'),
              Tab(text: 'Delivered'),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            _buildOrdersToDeliverList(),
            _buildDeliveredList(),
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

  Widget _buildOrdersToDeliverList() {
    return ListView(
      children: const [
        // Placeholder for orders to deliver
        Card(
          child: ListTile(
            title: Text('Order #0004'),
            subtitle: Text('Pending'),
          ),
        ),
      ],
    );
  }

  Widget _buildDeliveredList() {
    return ListView(
      children: const [
        // Placeholder for delivered orders
        Card(
          child: ListTile(
            title: Text('Order #0001'),
            subtitle: Text('Delivered'),
          ),
        ),
      ],
    );
  }
}
