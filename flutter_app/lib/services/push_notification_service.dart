/// This file defines the `PushNotificationService`, which encapsulates all logic
/// for handling Firebase Cloud Messaging (FCM) in the application.

import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:cloud_firestore/cloud_firestore.dart';

// This must be a top-level function (not a class method) to handle messages
// when the app is in the background or terminated.
@pragma('vm:entry-point')
Future<void> firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  // Background message handling logic goes here.
  // For example, you could initialize Firebase if it's not already.
  // await Firebase.initializeApp();
  print("Handling a background message: ${message.messageId}");
}

/// A service class to manage push notification setup and handling.
class PushNotificationService {
  // The instance of Firebase Messaging.
  final FirebaseMessaging _fcm = FirebaseMessaging.instance;
  // An instance of Firebase Firestore for database operations.
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;

  /// Initializes the push notification service.
  ///
  /// This method requests permissions, sets up listeners for different message states
  /// (foreground, background tap), and should be called once at app startup.
  Future<void> initialize() async {
    // Request notification permissions from the user (required for iOS).
    await _fcm.requestPermission();

    // --- Foreground Message Handling ---
    // This listener is triggered when a notification is received while the app is open.
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      print('Got a message whilst in the foreground!');
      print('Message data: ${message.data}');

      if (message.notification != null) {
        print('Message also contained a notification: ${message.notification}');
        // To show a visual notification (heads-up display) when the app is in the
        // foreground, you would typically use a local notifications plugin
        // like `flutter_local_notifications` here.
      }
    });

    // --- Background/Terminated Notification Tap Handling ---
    // This listener is triggered when the user taps on a notification, opening the
    // app from a background or terminated state.
    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      print('A new onMessageOpenedApp event was published!');
      // Here, you would typically navigate to a specific screen based on the
      // data in the message. For example, to an order details screen.
      // e.g., navigatorKey.currentState.pushNamed('/order', arguments: message.data['orderId']);
    });
  }

  /// Retrieves the unique FCM token for this device.
  ///
  /// The token can be null if it has not been generated yet.
  Future<String?> getToken() async {
    try {
      return await _fcm.getToken();
    } catch (e) {
      print('Error getting FCM token: $e');
      return null;
    }
  }

  /// Saves the FCM token to the user's document in Firestore.
  ///
  /// This allows the backend to send targeted notifications to this user.
  /// - [userId]: The ID of the user whose document should be updated.
  Future<void> saveTokenToDatabase(String userId) async {
    try {
      // Get the current token.
      String? token = await getToken();
      if (token != null) {
        // If the token exists, update the user's document in Firestore.
        await _firestore.collection('users').doc(userId).update({
          'fcmToken': token,
        });
      }
    } catch (e) {
      // Log any errors that occur during the database operation.
      print('Error saving token to database: $e');
    }
  }
}
