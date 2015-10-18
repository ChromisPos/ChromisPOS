18th October
Release: v0.52

Bug #6 - Multi install error - fixed
Bug #8 - Ticket.Buttons - fixed error
Bug #10 - Barcode printing, issue printing some barcode type in reports - fixed


Automatic barcode type recognition routine added. When a product is saved the type 
of barcode is calculated using some basic formulas
* if it contains no numeric characters, is is flagged as CODE128
* 7 digits with correct checksum - UPC-E
* 8 digits with correct checksum - EAN-8
* 12 digits with correct checksum - UPC-A
* 13 digits with correct checksum - EAN-13
* 14 digits with correct checksum - GTIN

Any code above with the incorrect checksum if defined as null, this is is required as 
some scanners will reject the code. This is not used for general scanning only when 
printing reports.

Drag'n'drop images into chromis for stock records.

Updated ready for the new version of the Kitchen Screen Application V1.50. 

More general tidy up of the code.

Merged changes from John Barrett in to the main code.



21st September 2015
Release: v0.51

Fixed SQL errors in Convert and it now informs the the user of its progress via a progress bar.


# Default Icon colours changed.
# Added the ability for the user to change the colout of the icons. Located in the install folder
  is folder called icon sets, copy the the required jar to the lIb folder to changethe colours
# Identified bug in look and feel, unrequired lib file, now fixed
# Found issue of missing field in products when coming from 3.70  - fixed
# Found issue with rightslevel moving from 3.70 - fixed
# Update ticket.buttons to point to image library for built in buttons. Maintains consistency
# New shortcut icons now in use - thanks Fanzam 
# Text version of permissions now deactivated by default, On custom permissions will need to be added 
  to the database.
# Started to tidy the message dialog boxes.
# The main bug is fix in variable barcodes, these have now been written to comply with GS1 UK & GS1 US. 
  Included is a pdf which explains how these barcodes work and how to set them up correctly in Chromis.
# Plus a number of bug fixes supplied by John Barrett, thanks John.


bug #5
All icons set to use 18x18 size to maintain consistency - fixed


