# FBU Final Intern Project - Furnishare

Furnishare is a mobile commerce app that connects students of the same college and facilitates the process of finding furniture and household items for sale.

## MVP:

- [X] User can log in/out and sign up with school email
- [X] User should be able to search for an item specifying desired criteria
- [X] User can post an item for sale
- [X] User can view their profile as well as others
- [X] User can see posts on timeline and refresh for new feed

## Stretch Stories

- [X] Direct messaging
- [ ] Endless scrolling
- [X] Bio for each user
- [X] Profile pictures
- [ ] Adding more animations and styling/colors
- [ ] Commenting on posts
- [ ] Create Post details activity when post is selected

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
         - Category
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

   Uses a sorting algorithm to sort search results by relevance using 5 criteria (the required fields when creating a new post).
   
## Challenges I faced and skills I learned while building this app:

Week 1:
- Being able to upload a photo from gallery
- Figuring out how to implement a toolbar in order to add a menu item
- Figuring out what to get and read a JSONArray with no key
- Learned how to use AutoCompleteTextView

Week 2:
- How to retrieve data from another pointer subclass in Parse
- Learned how to use a Spinner
- Learned how to use a ScrollView
- Learned how to use an ImageButton and selector to make an icon change state when clicked on

Week 3:
- How to create an Array in Parse and retrieve data from it.

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
 
