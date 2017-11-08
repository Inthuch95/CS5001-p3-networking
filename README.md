## Returning of Binary Images
The server checks if the request contains image file extension (jpg, gif, png) and returns binary image if it exists.

## Multithreading
The server supports multiple concurrent client connection requests up to 50 connections.

## Logging
Each time requests are	made, their logs are stored in a file called log.txt in the root directory. A log can contain date/time, request, response code.  
