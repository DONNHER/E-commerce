import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/push_notification_service.dart';
import 'register_page.dart'; // make sure this exists
import 'admin_dashboard_page.dart';
import 'seller_dashboard_page.dart';
import 'rider_dashboard_page.dart';
import 'main_page.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({Key? key}) : super(key: key);
  static const routeName = '/login';

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final TextEditingController _email = TextEditingController();
  final TextEditingController _pass = TextEditingController();
  bool _loading = false;
  bool _obscure = true;

  Future<void> _login() async {
    setState(() => _loading = true);
    try {
      final userCredential = await FirebaseAuth.instance.signInWithEmailAndPassword(
        email: _email.text.trim(),
        password: _pass.text,
      );

      if (userCredential.user != null) {
        final user = userCredential.user!;
        await Provider.of<PushNotificationService>(context, listen: false).saveTokenToDatabase(user.uid);

        // Check user role and redirect
        final adminDoc = await FirebaseFirestore.instance.collection('admins').doc(user.uid).get();
        if (adminDoc.exists) {
          Navigator.of(context).pushReplacementNamed(AdminDashboardPage.routeName);
          return;
        }

        final sellerDoc = await FirebaseFirestore.instance.collection('sellers').doc(user.uid).get();
        if (sellerDoc.exists) {
          Navigator.of(context).pushReplacementNamed(SellerDashboardPage.routeName);
          return;
        }

        final customerDoc = await FirebaseFirestore.instance.collection('customers').doc(user.uid).get();
        if (customerDoc.exists) {
          Navigator.of(context).pushReplacementNamed(MainPage.routeName);
          return;
        }

        // Fallback for any other case (or if user doc not found)
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('User role not found. Please contact support.')),
        );

      }
    } on FirebaseAuthException catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.message ?? 'Auth error')),
      );
    } finally {
      setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    const Color lightMint = Color(0xFFE6F5EC); // match register
    const Color accentGreen = Color(0xFF2CB57A);

    return Scaffold(
      backgroundColor: lightMint,
      body: SafeArea(
        child: Stack(
          children: [
            Column(
              children: [
                Padding(
                  padding: const EdgeInsets.only(left: 8, top: 8),
                  child: Align(
                    alignment: Alignment.centerLeft,
                    child: IconButton(
                      icon: const Icon(Icons.arrow_back, color: Colors.black87),
                      onPressed: () => Navigator.pop(context),
                    ),
                  ),
                ),
                Expanded(
                  child: SingleChildScrollView(
                    child: Column(
                      children: [
                        const SizedBox(height: 10),
                        // Title
                        const Text(
                          'Log In',
                          style: TextStyle(
                            fontSize: 28,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 34),

                        // Email
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 26),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const Text(
                                'Email',
                                style: TextStyle(
                                  fontSize: 16,
                                  color: Colors.black87,
                                ),
                              ),
                              const SizedBox(height: 8),
                              TextField(
                                controller: _email,
                                keyboardType: TextInputType.emailAddress,
                                decoration: InputDecoration(
                                  filled: true,
                                  fillColor: Colors.white,
                                  contentPadding: const EdgeInsets.symmetric(
                                    horizontal: 22,
                                    vertical: 17,
                                  ),
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(36),
                                    borderSide: BorderSide.none,
                                  ),
                                ),
                              ),
                              const SizedBox(height: 18),

                              // Password
                              const Text(
                                'Password',
                                style: TextStyle(
                                  fontSize: 16,
                                  color: Colors.black87,
                                ),
                              ),
                              const SizedBox(height: 8),
                              TextField(
                                controller: _pass,
                                obscureText: _obscure,
                                decoration: InputDecoration(
                                  filled: true,
                                  fillColor: Colors.white,
                                  contentPadding: const EdgeInsets.symmetric(
                                    horizontal: 22,
                                    vertical: 17,
                                  ),
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(36),
                                    borderSide: BorderSide.none,
                                  ),
                                  suffixIcon: IconButton(
                                    icon: Icon(
                                      _obscure
                                        ? Icons.visibility_off_outlined
                                        : Icons.visibility,
                                      color: accentGreen,
                                    ),
                                    onPressed: () =>
                                      setState(() => _obscure = !_obscure),
                                  ),
                                ),
                              ),
                              const SizedBox(height: 8),
                              Align(
                                alignment: Alignment.centerRight,
                                child: TextButton(
                                  onPressed: () {
                                    // Add forgot password logic if needed
                                  },
                                  child: const Text(
                                    'Forgot Password?',
                                    style: TextStyle(color: Colors.black87),
                                  ),
                                ),
                              ),
                              const SizedBox(height: 24),
                              SizedBox(
                                width: double.infinity,
                                height: 53,
                                child: _loading
                                  ? const Center(child: CircularProgressIndicator())
                                  : ElevatedButton(
                                      style: ElevatedButton.styleFrom(
                                        backgroundColor: accentGreen,
                                        shape: RoundedRectangleBorder(
                                          borderRadius: BorderRadius.circular(32),
                                        ),
                                      ),
                                      onPressed: _login,
                                      child: const Text(
                                        'Log In',
                                        style: TextStyle(
                                          fontSize: 18,
                                          fontWeight: FontWeight.w600,
                                        ),
                                      ),
                                    ),
                              ),
                            ],
                          ),
                        ),

                        const SizedBox(height: 22),
                        const Text('Or Log In with', style: TextStyle(fontSize: 15)),
                        const SizedBox(height: 18),

                        // Social buttons
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            GestureDetector(
                              onTap: () {},
                              child: CircleAvatar(
                                radius: 32,
                                backgroundColor: Colors.white,
                                child: Icon(Icons.facebook,
                                  color: Colors.blue[800],
                                  size: 38,
                                ),
                              ),
                            ),
                            const SizedBox(width: 34),
                            GestureDetector(
                              onTap: () {},
                              child: const CircleAvatar(
                                radius: 32,
                                backgroundColor: Colors.white,
                                child: Icon(Icons.g_mobiledata,
                                  color: Colors.redAccent,
                                  size: 38,
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 42),
                        Padding(
                          padding: const EdgeInsets.only(bottom: 16),
                          child: Wrap(
                            crossAxisAlignment: WrapCrossAlignment.center,
                            children: [
                              const Text(
                                "Don't have an account? ",
                                style: TextStyle(
                                  color: Colors.black87,
                                  fontSize: 15,
                                ),
                              ),
                              GestureDetector(
                                onTap: () => Navigator.pushNamed(
                                  context,
                                  RegisterPage.routeName,
                                ),
                                child: const Text(
                                  'Sign Up',
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    color: Colors.black87,
                                    fontSize: 15,
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
