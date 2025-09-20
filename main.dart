import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:url_launcher/url_launcher.dart';
import 'dart:async';
import 'package:flutter/services.dart';

void main() {
  runApp(ShakeToSOSApp());
}

class ShakeToSOSApp extends StatefulWidget {
  @override
  State<ShakeToSOSApp> createState() => _ShakeToSOSAppState();
}

class _ShakeToSOSAppState extends State<ShakeToSOSApp> {
  static const platform = MethodChannel('shaketosos/foreground');
  bool serviceEnabled = true;
  List<String> contacts = [];

  @override
  void initState() {
    super.initState();
    loadPrefs();
  }

  Future<void> loadPrefs() async {
    final p = await SharedPreferences.getInstance();
    setState(() {
      contacts = p.getStringList('contacts') ?? [];
      serviceEnabled = p.getBool('serviceEnabled') ?? true;
    });
  }

  Future<void> savePrefs() async {
    final p = await SharedPreferences.getInstance();
    await p.setStringList('contacts', contacts);
    await p.setBool('serviceEnabled', serviceEnabled);
  }

  Future<void> toggleService(bool val) async {
    setState(() => serviceEnabled = val);
    await savePrefs();
    try {
      await platform.invokeMethod('setServiceEnabled', {'enabled': val});
    } on PlatformException catch (e) {
      print('Platform error: $e');
    }
  }

  Future<void> testSOS() async {
    final message = Uri.encodeComponent('SOS! I need help. Location: https://maps.google.com?q=0,0');
    // open SMS app
    final smsUri = Uri.parse('sms:?body=$message');
    if (await canLaunchUrl(smsUri)) {
      await launchUrl(smsUri);
    }
  }

  void addContact() async {
    final controller = TextEditingController();
    await showDialog(context: context, builder: (_) {
      return AlertDialog(
        title: Text('Add contact (phone number)'),
        content: TextField(controller: controller, keyboardType: TextInputType.phone),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: Text('Cancel')),
          TextButton(onPressed: () {
            final txt = controller.text.trim();
            if (txt.isNotEmpty) {
              setState(() => contacts.add(txt));
              savePrefs();
            }
            Navigator.pop(context);
          }, child: Text('Add')),
        ],
      );
    });
  }

  void removeContact(int idx) {
    setState(() => contacts.removeAt(idx));
    savePrefs();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'ShakeToSOS',
      home: Scaffold(
        appBar: AppBar(title: Text('ShakeToSOS')),
        body: Padding(
          padding: EdgeInsets.all(12),
          child: Column(
            children: [
              SwitchListTile(
                title: Text('Background service (auto-start on boot enabled)'),
                value: serviceEnabled,
                onChanged: (v) => toggleService(v),
              ),
              ElevatedButton(onPressed: testSOS, child: Text('Test SOS')),
              SizedBox(height: 12),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text('Emergency Contacts', style: TextStyle(fontWeight: FontWeight.bold)),
                  ElevatedButton(onPressed: addContact, child: Text('Add')),
                ],
              ),
              Expanded(
                child: ListView.builder(
                  itemCount: contacts.length,
                  itemBuilder: (_,i) => ListTile(
                    title: Text(contacts[i]),
                    trailing: IconButton(icon: Icon(Icons.delete), onPressed: () => removeContact(i)),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
