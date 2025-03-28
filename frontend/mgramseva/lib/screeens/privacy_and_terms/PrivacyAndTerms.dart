import 'package:flutter/material.dart';
import 'package:mgramseva/screeens/privacy_and_terms/PrivacyWidget.dart';
import 'package:provider/provider.dart';

import '../../providers/language.dart';
import '../../routers/routers.dart';
import '../../widgets/footer.dart';
import 'TermsAndConditionWidget.dart';

class PrivacyAndTerms extends StatefulWidget {
  final String pageType;
  final bool showLeading;
  const PrivacyAndTerms(
      {Key? key, required this.pageType, this.showLeading = false})
      : super(key: key);

  @override
  State<PrivacyAndTerms> createState() => _PrivacyAndTermsState();
}

class _PrivacyAndTermsState extends State<PrivacyAndTerms> {
  @override
  Widget build(BuildContext context) {
    var languageProvider =
        Provider.of<LanguageProvider>(context, listen: false);
    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.background,
      appBar: AppBar(
        toolbarHeight: 80,
        iconTheme: IconThemeData(
          color: Colors.white, // Change icon color here
        ),
        titleSpacing: 0,
        centerTitle: true,
        automaticallyImplyLeading: false, // Disable default back button
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
          ),
          color: Colors.black,
          // Back button icon
          onPressed: () {
            Navigator.pop(context); // Go back to the previous screen
          },
        ),
        // Hide if showLeading is false
        title: Image(
          height: 60,
          width: 70,
          fit: BoxFit.contain,
          image: NetworkImage(
            languageProvider.stateInfo!.logoUrlWhite!,
          ),
        ),
      ),
      // appBar: AppBar(
      //   toolbarHeight: 80,
      //   iconTheme: IconThemeData(
      //     color: Colors.white, //change your color here
      //   ),
      //   titleSpacing: 0,
      //   centerTitle: true,
      //   automaticallyImplyLeading: widget.showLeading,
      //   title: Image(
      //       height: 60,
      //       width: 70,
      //       fit: BoxFit.contain,
      //       image: NetworkImage(
      //         languageProvider.stateInfo!.logoUrlWhite!,
      //       )),
      // ),
      body: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Container(
              alignment: Alignment.center,
              width: MediaQuery.of(context).size.width > 900
                  ? MediaQuery.of(context).size.width * 0.55
                  : MediaQuery.of(context).size.width > 800
                      ? MediaQuery.of(context).size.width * 0.75
                      : MediaQuery.of(context).size.width * 0.95,
              child: Card(
                child: SingleChildScrollView(
                  child: Column(
                    children: [
                      widget.pageType == Routes.PRIVACY_POLICY
                          ? PrivacyWidget()
                          : TermsAndConditionWidget(),
                      Align(alignment: Alignment.bottomCenter, child: Footer())
                    ],
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
