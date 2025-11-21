import 'package:flutter/material.dart';

class OrderSuccessPage extends StatelessWidget {
  const OrderSuccessPage({Key? key}) : super(key: key);

  static const routeName = '/order-success';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFE6F4F1),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            // Image
            Image.asset(
              'assets/images/xcv.png', // Placeholder - you might need to add this image
              height: 151,
              width: 199,
              errorBuilder: (c, e, s) => const Icon(Icons.check_circle_outline, size: 150, color: Colors.green),
            ),
            const SizedBox(height: 12),
            // Title
            const Text(
              'Congratulations!',
              style: TextStyle(fontSize: 25, fontWeight: FontWeight.bold, color: Colors.black),
            ),
            const SizedBox(height: 5),
            const Text(
              'Order Placed',
              style: TextStyle(fontSize: 18, color: Colors.black),
            ),
            const SizedBox(height: 30),
            // Order Details
            Card(
              color: Colors.yellow, // Approximating @drawable/yell
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  children: [
                    _buildDetailRow('Name', 'Andre Dulay'),
                    _buildDetailRow('Date', 'July 24, 2025'),
                    _buildDetailRow('Time Ordered', '12:00 pm'),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 50),
            // Back to Home Button
            ElevatedButton(
              onPressed: () {
                Navigator.of(context).popUntil((route) => route.isFirst);
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2CB57A), // Approximating @drawable/green
                minimumSize: const Size(double.infinity, 50),
              ),
              child: const Text('Back to home', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 19)),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDetailRow(String title, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 5.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(title, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.black)),
          Text(value, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.black)),
        ],
      ),
    );
  }
}
