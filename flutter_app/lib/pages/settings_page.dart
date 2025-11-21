import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';
import '../providers/user_provider.dart';
import 'otp_verification_page.dart';
import 'landing_page.dart';
import 'change_password_page.dart';

class SettingsPage extends StatefulWidget {
  const SettingsPage({Key? key}) : super(key: key);

  static const routeName = '/settings';

  @override
  State<SettingsPage> createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  File? _pickedImage;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<UserProvider>(context, listen: false).fetchUser();
    });
  }

  Future<void> _pickImage() async {
    final picker = ImagePicker();
    final pickedImageFile = await picker.pickImage(source: ImageSource.gallery);
    if (pickedImageFile != null) {
      setState(() {
        _pickedImage = File(pickedImageFile.path);
      });
      // TODO: Upload image to Firebase Storage and update user profile URL in Firestore
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFE6F4F1),
      appBar: AppBar(
        title: const Text('Welcome'),
        backgroundColor: const Color(0xFFE6F4F1),
      ),
      body: Consumer<UserProvider>(
        builder: (context, userProvider, child) {
          final user = userProvider.user;
          return Padding(
            padding: const EdgeInsets.all(20.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Profile Picture
                Center(
                  child: Stack(
                    children: [
                      CircleAvatar(
                        radius: 60,
                        backgroundImage: _pickedImage != null
                            ? FileImage(_pickedImage!)
                            : const AssetImage('assets/images/regis.png') as ImageProvider, // Placeholder
                      ),
                      Positioned(
                        bottom: 0,
                        right: 0,
                        child: IconButton(
                          icon: const Icon(Icons.edit),
                          onPressed: _pickImage,
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 10),
                Center(child: Text(user?.name ?? '', style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold))),
                Center(child: Text(user?.email ?? '')),
                const SizedBox(height: 20),
                // Account Section
                const Text('Account', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                const SizedBox(height: 10),
                _buildSettingsTile(context, 'Change your password', () {
                  Navigator.of(context).pushNamed(ChangePasswordPage.routeName);
                }),
                _buildSettingsTile(context, 'Change Email', () {}),
                _buildSettingsTile(context, 'Add Phone Number', () {
                  Navigator.of(context).pushNamed(OtpVerificationPage.routeName);
                }),
                _buildSettingsTile(context, 'Security and privacy', () {}),
                const SizedBox(height: 20),
                // Logout Button
                ElevatedButton(
                  onPressed: () {
                    userProvider.logout();
                    Navigator.of(context).pushReplacementNamed(LandingPage.routeName);
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.red,
                    minimumSize: const Size(double.infinity, 37),
                  ),
                  child: const Text('LOG OUT'),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildSettingsTile(BuildContext context, String title, VoidCallback onTap) {
    return Card(
      color: const Color(0xFF2CB57A),
      margin: const EdgeInsets.only(bottom: 5),
      child: ListTile(
        title: Text(title, style: const TextStyle(color: Colors.black)),
        trailing: const Icon(Icons.arrow_forward_ios, color: Colors.black),
        onTap: onTap,
      ),
    );
  }
}
