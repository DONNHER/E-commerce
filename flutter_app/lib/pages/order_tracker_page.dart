import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:provider/provider.dart';
import 'package:latlong2/latlong.dart';
import '../providers/order_provider.dart';
import '../models/order.dart';

class OrderTrackerPage extends StatefulWidget {
  const OrderTrackerPage({Key? key, this.order}) : super(key: key);

  final OrderModel? order;
  static const routeName = '/order-tracker';

  @override
  State<OrderTrackerPage> createState() => _OrderTrackerPageState();
}

class _OrderTrackerPageState extends State<OrderTrackerPage> {

  @override
  void initState() {
    super.initState();
    if (widget.order != null) {
      Provider.of<OrderProvider>(context, listen: false).trackOrder(widget.order!.id);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Track Order'),
      ),
      body: Consumer<OrderProvider>(
        builder: (context, orderProvider, child) {
          final order = widget.order != null ? orderProvider.orders.firstWhere((o) => o.id == widget.order!.id, orElse: () => widget.order!) : null;

          if (order == null) {
            return const Center(child: Text('Order not found.'));
          }

          return Column(
            children: [
              SizedBox(
                height: 300,
                child: FlutterMap(
                  options: MapOptions(
                    initialCenter: order.location != null ? LatLng(order.location!.latitude, order.location!.longitude) : const LatLng(0, 0),
                    initialZoom: 15.0,
                  ),
                  children: [
                    TileLayer(
                      urlTemplate: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                      subdomains: const ['a', 'b', 'c'],
                    ),
                    if (order.location != null)
                      MarkerLayer(
                        markers: [
                          Marker(
                            width: 80.0,
                            height: 80.0,
                            point: LatLng(order.location!.latitude, order.location!.longitude),
                            child: const Icon(Icons.location_on, color: Colors.red, size: 40),
                          ),
                        ],
                      ),
                  ],
                ),
              ),
              Expanded(
                child: ListView(
                  padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 8),
                  children: [
                    _buildTimelineStep('Order Placed', 'We have received your order.', order.status != 'Pending'),
                    _buildTimelineStep('Order Confirmed', 'Your order has been confirmed.', order.status == 'Confirmed' || order.status == 'Processing' || order.status == 'Ready to Pickup' || order.status == 'Delivered'),
                    _buildTimelineStep('Order Processed', 'We are preparing your order.', order.status == 'Processing' || order.status == 'Ready to Pickup' || order.status == 'Delivered'),
                    _buildTimelineStep('Ready to Pickup', 'Your order is ready for pickup.', order.status == 'Ready to Pickup' || order.status == 'Delivered'),
                    _buildTimelineStep('Delivered', 'Your order has been delivered.', order.status == 'Delivered'),
                  ],
                ),
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildTimelineStep(String title, String subtitle, bool isCompleted) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Column(
          children: [
            Icon(
              isCompleted ? Icons.check_circle : Icons.radio_button_unchecked,
              color: isCompleted ? Colors.green : Colors.grey,
            ),
            if (title != 'Delivered') // Don't draw a line after the last step
              Container(
                height: 32,
                width: 2,
                color: Colors.grey,
              ),
          ],
        ),
        const SizedBox(width: 10),
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
            Text(subtitle, style: const TextStyle(color: Colors.grey)),
          ],
        ),
      ],
    );
  }
}
