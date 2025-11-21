// import 'package:flutter_test/flutter_test.dart';
// import 'package:flutter/material.dart';
// import 'package:provider/provider.dart';
// import 'package:flutter_app/pages/main_page.dart';
// import 'package:flutter_app/services/temp_storage.dart';

// void main() {
//   testWidgets('MainPage smoke test', (WidgetTester tester) async {
//     await tester.pumpWidget(
//       ChangeNotifierProvider<TempStorage>(
//         create: (_) => TempStorage.instance,
//         child: const MaterialApp(home: MainPage()),
//       ),
//     );

//     await tester.pumpAndSettle();
//     expect(find.text('Popular Food'), findsOneWidget);
//   });
// }
