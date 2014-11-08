UTEID: rsp835; dsj398;
FIRSTNAME: Rohit; Danish;
LASTNAME: Pattanaik; Jaffer;
CSACCOUNT: rsp615; danish;
EMAIL: rohit.pattanaik@utexas.edu; danish.jaffer@utexas.edu;

[Description]
There are 4 java files. AES contains the main method and simply parses the arguments and uses the appropriate objects. Tables contains the tables used in the algorithms. Encryptor and Decryptor are identical in structure. One has the appropriate methods to encrypt and the other to decrypt. Each has its own versions of computing round keys, subBytes, shiftRows and mixColumns.
The encrypt and decrypt methods call these functions in the appropriate order.
The code can be compiled by typing
javac *.java
The code is executed with:
java AES option keyFile inputFile

[Finish]
We finished all of the project. The throughput may not be reporting correctly because of a calculation error.

[Test Case 1]
Text: plaintext
Key: key

[Output]
Encrypting
Encryption Complete
Bytes processed: 16.0
Time taken (s): 0.007
Throughput: 0.0021798270089285715MB/s

Decrypting
Encryption Complete
Bytes processed: 16.0
Time taken (s): 0.005
Throughput: 0.0030517578125MB/s

[Test Case 2]
Text: testtext
Key: testkey

[Output]
Encrypting
Encryption Complete
Bytes processed: 16.0
Time taken (s): 0.006
Throughput: 0.0025431315104166665MB/s

Decrypting
Encryption Complete
Bytes processed: 16.0
Time taken (s): 0.008
Throughput: 0.0019073486328125MB/s

[Test Case 3]
Text: longtext
Key: longkey

[Output]
Encrypting
Error: Key is not 128 bits. Aborting Encryption
Encryption failed

[Test Case 4]
Text: multilinetext
Key: testkey

[Output]
Encrypting
Encryption Complete
Bytes processed: 3820304.0
Time taken (s): 5.636
Throughput: 0.6464382195997271MB/s

Decrypting
Encryption Complete
Bytes processed: 3820304.0
Time taken (s): 6.026
Throughput: 0.6046010298148129MB/s
