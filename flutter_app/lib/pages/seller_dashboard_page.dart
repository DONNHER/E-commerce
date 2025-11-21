import 'package:flutter/material.dart';

class SellerDashboardPage extends StatelessWidget {
  const SellerDashboardPage({Key? key}) : super(key: key);

  static const routeName = '/seller-dashboard';

  @override
  Widget build(BuildContext context) {
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
        body: TabBarView(
          children: [
            _buildOrdersInnerTab(),
            _buildOrdersInnerTab(),
            _buildOrdersInnerTab(),
          ],
        ),
        bottomNavigationBar: BottomAppBar(
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              IconButton(icon: const Icon(Icons.history), onPressed: () {}),
              IconButton(icon: const Icon(Icons.person), onPressed: () {}),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildOrdersInnerTab() {
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
            _buildOrderList(),
            _buildOrderList(),
            _buildOrderList(),
          ],
        ),
      ),
    );
  }

  Widget _buildOrderList() {
    return ListView(
      children: const [
        // Placeholder for order cards
        Card(
          child: ListTile(
            leading: Icon(Icons.coffee),
            title: Text('LOKAL KOFI'),
            subtitle: Text('Iced coffee/Mocha\nQuantity: 1x\nOrder #0001'),
          ),
        ),
        Card(
          child: ListTile(
            leading: Icon(Icons.local_drink),
            title: Text('COOL ROYAL'),
            subtitle: Text('Milktea/Watermelon\nQuantity: 1x\nOrder #0002'),
          ),
        ),
        Card(
          child: ListTile(
            leading: Icon(Icons.cake),
            title: Text('CAFE LUIZA'),
            subtitle: Text('Blueberry Cheesecake\nQuantity: 3x\nOrder #0003'),
          ),
        ),
      ],
    );
  }
}
