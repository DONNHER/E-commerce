import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/push_notification_service.dart';
import 'user_dashboard_page.dart';

class RegisterPage extends StatefulWidget {
  const RegisterPage({Key? key}) : super(key: key);
  static const routeName = '/register';

  @override
  State<RegisterPage> createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final _name = TextEditingController();
  final _email = TextEditingController();
  bool _loading = false;

  Future<void> _register() async {
    setState(() => _loading = true);
    try {
      final userCredential = await FirebaseAuth.instance.createUserWithEmailAndPassword(
        email: _email.text.trim(),
        password: 'DefaultPassword123!', // Add password logic if needed
      );

      if (userCredential.user != null) {
        await Provider.of<PushNotificationService>(context, listen: false).saveTokenToDatabase(userCredential.user!.uid);
      }

      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('Registered')));

      // TODO: Add logic to save the user's role to your database

      Navigator.of(context).pushReplacementNamed(UserDashboardPage.routeName);
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
    const lightMint = Color(0xFFE6F5EC); // As PNG background
    const accentGreen = Color(0xFF2CB57A);

    return Scaffold(
      backgroundColor: lightMint,
      body: SafeArea(
        child: Stack(
          children: [
            // Content
            Column(
              children: [
                // Top row with back arrow
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
                        // Large avatar
                        CircleAvatar(
                          radius: 67,
                          backgroundColor: accentGreen.withOpacity(0.26),
                          child: CircleAvatar(
                            radius: 58,
                            backgroundColor: accentGreen.withOpacity(0.16),
                            child: CircleAvatar(
                              radius: 55,
                              backgroundColor: Colors.transparent,
                              child: Icon(Icons.person, size: 77, color: Colors.grey[700]),
                            ),
                          ),
                        ),
                        const SizedBox(height: 14),
                        const Text(
                          'Sign Up',
                          style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 34),

                        // Name label and field
                        Padding(
                          padding: const EdgeInsets.only(left: 26, right: 26),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const Text('Name', style: TextStyle(fontSize: 16, color: Colors.black87)),
                              const SizedBox(height: 8),
                              TextField(
                                controller: _name,
                                decoration: InputDecoration(
                                  filled: true,
                                  fillColor: Colors.white,
                                  contentPadding: const EdgeInsets.symmetric(horizontal: 22, vertical: 17),
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(36),
                                    borderSide: BorderSide.none,
                                  ),
                                ),
                              ),
                              const SizedBox(height: 18),
                              const Text('Email', style: TextStyle(fontSize: 16, color: Colors.black87)),
                              const SizedBox(height: 8),
                              TextField(
                                controller: _email,
                                decoration: InputDecoration(
                                  filled: true,
                                  fillColor: Colors.white,
                                  contentPadding: const EdgeInsets.symmetric(horizontal: 22, vertical: 17),
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(36),
                                    borderSide: BorderSide.none,
                                  ),
                                ),
                              ),
                              const SizedBox(height: 24),
                            ],
                          ),
                        ),

                        // Big green sign up button
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 26),
                          child: SizedBox(
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
                                    onPressed: _register,
                                    child: const Text('Sign Up', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
                                  ),
                          ),
                        ),

                        const SizedBox(height: 20),
                        const Text('Or Login With', style: TextStyle(fontSize: 15)),
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
                                child: Icon(Icons.facebook, color: Colors.blue[800], size: 38),
                              ),
                            ),
                            const SizedBox(width: 34),
                            GestureDetector(
                              onTap: () {},
                              child: const CircleAvatar(
                                radius: 32,
                                backgroundColor: Colors.white,
                                child: Icon(Icons.g_mobiledata, color: Colors.redAccent, size: 38),
                              ),
                            ),
                          ],
                        ),

                        const SizedBox(height: 42),
                        Padding(
                          padding: const EdgeInsets.only(bottom: 16),
                          child: RichText(
                            text: const TextSpan(
                              style: TextStyle(color: Colors.black87, fontSize: 15),
                              children: [
                                TextSpan(text: 'Already have an account? '),
                                TextSpan(
                                  text: 'Log in',
                                  style: TextStyle(fontWeight: FontWeight.bold),
                                  // handle navigation if desired
                                ),
                              ],
                            ),
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
