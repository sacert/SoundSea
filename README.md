# Song Sea

Allows the user to download songs by entering in a song title which will include a variety of metadata as well as album cover.

![alt tag](https://raw.github.com/sacert/SoundSea/master/SoundSeaDemo.gif)

Note: If your song isn't being properly searching, try adding song **title** and **artist**. To strengthen search further, add **album name**.

#####How it works? :
   - It uses the itunes search to grab the metadata that the user has requested
   - Use Pleer.com to find and download corresponding song
   
#####Features:
   - Shows a list of songs from the search made
   - Filters through high, low, or VBR bitrates
   - Ability to play songs within the browser
   - Quick download to quickly download to instantly download the first search result

#####Song List:
Filter through the songs using the `<` and `>` keys once shown.

#####Settings:
   - Click on the Settings button on the top left to access settings
   - Set directory
   - Change song filter quality
   
<img src="https://raw.github.com/sacert/SoundSea/master/SettingsWindow.png" width="400" height="171"/>

#####Play Song:
   - Hit the play button on the bottom left of the album art
   - Hit again to pause the song
   - If changed song from list, pause and start the song again to play the current song in the list

#####Working on:
   - Cleaning up code - as of right now there are a ton of global variables that I want to remove and lines of code that are called multiple times, it is best to make functions out of them
   - Bug fixing - need to play around with it more for certain bugs to pop up
   - Tighten the searching algorithm
   
   
