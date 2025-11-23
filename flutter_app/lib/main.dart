/// This is the main entry point for the Calayo e-commerce Flutter application.
///
/// It handles the initialization of essential services like Firebase and Push Notifications,
/// sets up global error handling to prevent crashes, and configures the root widget
/// tree with state management providers and navigation routes.

import 'dart:async';
import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:provider/provider.dart';

// Import all pages and services to set up navigation and providers.
import 'pages/landing_page.dart';
import 'services/temp_storage.dart';
import 'services/push_notification_service.dart';
import 'providers/cart_provider.dart';
import 'providers/user_provider.dart';
import 'providers/order_provider.dart';
import 'pages/products_page.dart';
import 'pages/search_page.dart';
import 'pages/cart_page.dart';
import 'pages/add_to_cart_page.dart';
import 'pages/checkout_page.dart';
import 'pages/login_page.dart';
import 'pages/register_page.dart';
import 'pages/main_page.dart';
import 'pages/add_address_page.dart';
import 'pages/add_product_page.dart';
import 'pages/order_history_page.dart';
import 'pages/order_tracker_page.dart';
import 'pages/settings_page.dart';
import 'pages/order_success_page.dart';
import 'pages/splash_page.dart';
import 'pages/admin_dashboard_page.dart';
import 'pages/seller_dashboard_page.dart';
import 'pages/rider_dashboard_page.dart';
import 'pages/otp_verification_page.dart';
import 'pages/product_details_page.dart';
import 'pages/search_results_page.dart';
import 'pages/change_password_page.dart';
import 'pages/order_details_page.dart';
import 'models/item.dart';
import 'models/order.dart';

/// The main function of the application.
void main() {
  // `runZonedGuarded` is a mechanism to catch all uncaught errors and exceptions
  // in a specific code zone, preventing the app from crashing.
  runZonedGuarded<Future<void>>(() async {
    // Ensures that the Flutter widget binding is initialized. This is required
    // before using any plugins or platform-specific code.
    WidgetsFlutterBinding.ensureInitialized();

    // Initializes the connection to the Firebase backend.
    await Firebase.initializeApp();

    // Initializes the push notification service to handle incoming messages.
    await PushNotificationService().initialize();

    // Sets a custom error handler for errors that occur within the Flutter framework.
    // This is useful for logging and debugging UI-related errors.
    FlutterError.onError = (FlutterErrorDetails details) {
      // Log the error details to the console for debugging.
      print("Caught Flutter error: ${details.exception} \nStacktrace: ${details.stack}");
    };

    // Runs the main application widget.
    runApp(const MyApp());
  }, (error, stack) {
    // This is the global error handler for any other Dart errors that were not caught.
    // Log the error and stack trace to the console.
    print("Caught Dart error: $error \nStacktrace: $stack");
  });
}

/// The root widget of the application.
///
/// `MyApp` is a [StatelessWidget] that sets up the high-level structure of the app,
/// including state management providers and the main navigation system.
class MyApp extends StatelessWidget {
  /// Creates the root application widget.
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // `MultiProvider` is a widget from the `provider` package that makes multiple
    // providers available to all descendant widgets in the tree.
    return MultiProvider(
      providers: [
        // Provides a singleton instance of `TempStorage` for item data handling.
        ChangeNotifierProvider<TempStorage>(
            create: (_) => TempStorage.instance),
        // Provides an instance of `CartProvider` to manage the shopping cart state.
        ChangeNotifierProvider<CartProvider>(
            create: (_) => CartProvider()),
        // Provides an instance of `UserProvider` to manage user authentication and data.
        ChangeNotifierProvider<UserProvider>(
            create: (_) => UserProvider()),
        // Provides an instance of `OrderProvider` to manage user orders.
        ChangeNotifierProvider<OrderProvider>(
            create: (_) => OrderProvider()),
        // Provides a non-listening instance of `PushNotificationService` for access to its methods.
        Provider<PushNotificationService>(
          create: (_) => PushNotificationService(),
        ),
      ],
      // `MaterialApp` is the root of the app's visual structure.
      child: MaterialApp(
        title: 'E-commerce (Flutter)',
        // Defines the overall visual theme of the application.
        theme: ThemeData(primarySwatch: Colors.green),
        // Sets the initial screen of the app to the `SplashPage`.
        home: const SplashPage(),
        // Defines a map of named routes for simple, argument-less navigation.
        routes: {
          ProductsPage.routeName:      (_) => const ProductsPage(),
          SearchPage.routeName:        (_) => const SearchPage(),
          CartPage.routeName:          (_) => const CartPage(),
          AddToCartPage.routeName:     (_) => const AddToCartPage(),
          CheckoutPage.routeName:      (_) => const CheckoutPage(),
          LoginPage.routeName:         (_) => const LoginPage(),
          RegisterPage.routeName:      (_) => const RegisterPage(),
          MainPage.routeName:          (_) => const MainPage(),
          AddAddressPage.routeName:    (_) => const AddAddressPage(),
          AddProductPage.routeName:    (_) => const AddProductPage(),
          OrderHistoryPage.routeName:  (_) => const OrderHistoryPage(),
          OrderTrackerPage.routeName:  (_) => const OrderTrackerPage(),
          SettingsPage.routeName:      (_) => const SettingsPage(),
          OrderSuccessPage.routeName:  (_) => const OrderSuccessPage(),
          SplashPage.routeName:        (_) => const SplashPage(),
          LandingPage.routeName:       (_) => const LandingPage(),
          AdminDashboardPage.routeName:(_) => const AdminDashboardPage(),
          SellerDashboardPage.routeName:(_) => const SellerDashboardPage(),
          RiderDashboardPage.routeName:(_) => const RiderDashboardPage(),
          OtpVerificationPage.routeName:(_) => const OtpVerificationPage(),
          ChangePasswordPage.routeName:(_) => const ChangePasswordPage(),
        },
        // `onGenerateRoute` is a callback that is used for handling dynamic routing,
        // especially when arguments need to be passed to the new screen.
        onGenerateRoute: (settings) {
          // Handles navigation to the ProductDetailsPage, passing the selected `Item`.
          if (settings.name == ProductDetailsPage.routeName) {
            final args = settings.arguments as Item;
            return MaterialPageRoute(
              builder: (context) {
                return ProductDetailsPage(item: args);
              },
            );
          } 
          // Handles navigation to the SearchResultsPage, passing the search query string.
          else if (settings.name == SearchResultsPage.routeName) {
            final args = settings.arguments as String;
            return MaterialPageRoute(
              builder: (context) {
                return SearchResultsPage(searchQuery: args);
              },
            );
          } 
          // Handles navigation to the OrderDetailsPage, passing the selected `OrderModel`.
          else if (settings.name == OrderDetailsPage.routeName) {
            final args = settings.arguments as OrderModel;
            return MaterialPageRoute(
              builder: (context) {
                return OrderDetailsPage(order: args);
              },
            );
          }
          // If the route is not recognized, return null to let other mechanisms handle it.
          return null;
        },
      ),
    );
  }
}
