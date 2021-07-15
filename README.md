# FBU Final Intern Project - Furnitur Buy/Sell App

Furniture Buy/Sell App is a mobile commerce app that connects students of the same college and facilitates the process of selling furniture and household items.

## MVP:

- User can log in/out and sign up with school email
- User should be able to search for an item specifying desired criteria
- User can post an item for sale
- User can view their profile as well as others
- User can see posts on timeline and refresh for new feed

## Stretch Stories

- Different algorithms to sort search results (ie - chronological)
- Direct messaging
- Implementing Venmo API for in-app transactions
- Endless scrolling
- Bio for each user
- Adding more animations and styling/colors
- Commenting on posts

## Format: 
   
   Using multiple fragments controlled under main Bottom Navigation Menu.
   
   - Login Activity:
       - Allow user to log in or sign up
       
   - Profile fragment
       - Shows users current furniture they are selling
       - Show favorited posts
       - Allows user to log out
       - User can change contact/profile information

   - Search fragment
       - Allows user to search for furniture that they want through certain tags/criteria.
        - Searching criteria:
         - Color
         - Material
         - Measurements
         - Price
         - Condition

   - Home timeline fragment
       - Displays furniture other college students in the school are currently selling.

   - Post new furniture fragment:
       - Allow users to post images of furniture as well as select attributes.
       - Each furniture will have other fields that indicate SOLD, or ON HOLD, that the user can edit later on once they find a buyer.

## Animations: 

   Incorporate transition animations when moving between fragments.
   
## External Library: 

   Glide for images.
   
## Database: 

   Uses Parse to store user data.

## Gesture: 

   Implement double tap gesture to save posts.
   
## API: 

   Makes use of University Domains and Name API to allow the user to see posts from people at their school.

## Algorithm: 

   Use a sorting algorithm to sort search results by relevance (the required fields when creating a new post).


## License

    Copyright 2021 Luis Rivera

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 
