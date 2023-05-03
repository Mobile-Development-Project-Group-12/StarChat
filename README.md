# StarChat
A Simple Messaging application

<p align="center">
  <img src="https://user-images.githubusercontent.com/98469510/235798483-2998f840-0a6b-4f95-844d-8acda6b5c017.png" alt="StarChat logo" />
</p>

## Tables of contents
* [Our Technologies](#technologies-used)
* [Application Idea](#primary-Idea)
* [Our Target Demographic](#target-audience)
* [Workload](#who-did-what)
* [Design](#user-interface-basic-plan)
* [Deployment](#deployment)
* [Presentation](#presentation-slides)
* [VCS](#version-control-system)
* [Documentation](#documentation)
* [Final Application](#final-application)

## Technologies Used
This app was made using Kotlin and Jetpack compose, aswell as using firebases firestore, authentication, and storage for users. It follows an MVVM Architecture pattern, and it was made using an agile development framework such as kanban.

## Primary Idea
The idea of this application was to make the most simple, easy to use, messaging application ever seen.

## Target Audience
Our Target audience was middle aged people (40-49 years old). They would probably be the most interested in this application, but we think that anyone can use this application.

## Who Did What?
The Business part of this application was from Bobby Nodu, The Code and Code Documentation for this application was done by Daniel Mendes, and the Kanban and Github management was done by Bishal Rai. However, we had frequent meetings to collaborate on these things.

## User Interface Basic Plan

Below is a simple plan of how the app would look like. We already knew what we were making, so we didnt fully flesh out these drawings / designs.
<p align="center">
  <img alt="Screenshot 2023-03-20 at 04 28 44" src="https://user-images.githubusercontent.com/98469510/235802952-41fb8fff-5089-4a3f-93e2-51f66ef1bf90.png">
</p>

## Deployment
This app can be deployed by forking this repository and running it on your local android studio ide. Since no api keys are used, you can press start, and the app should work straight away. If you want to use your own database, delete the google-services.json file and add your own one.

## Presentation Slides
<img width="924" alt="Screenshot 2023-05-03 at 02 18 31" src="https://user-images.githubusercontent.com/98469510/235805376-41ecb817-6470-43dd-addd-9049bbe9773d.png">
<img width="924" alt="Screenshot 2023-05-03 at 02 18 49" src="https://user-images.githubusercontent.com/98469510/235805415-66896867-dcc7-422d-bd9f-3d89ace70768.png">
<img width="924" alt="Screenshot 2023-05-03 at 02 19 03" src="https://user-images.githubusercontent.com/98469510/235805448-dfa37bd7-d75b-41ec-adeb-c34fce4ea9f2.png">
<img width="924" alt="Screenshot 2023-05-03 at 02 19 19" src="https://user-images.githubusercontent.com/98469510/235805475-cde06697-5aa5-41e2-8837-3acca69d1e2b.png">

## Version Control System
Here is a screenshot of our completed Kanban Board:

<p align="center">
  <img width="355" alt="Screenshot 2023-05-03 at 02 25 23" src="https://user-images.githubusercontent.com/98469510/235806147-ea5d1291-0768-463b-96c2-8ba94f5203c9.png">
</p>

## Documentation.  
Coding is done in syntax that can be transformed into java-doc. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html#examples.

Example:  

  
```java
/**
* Class Calculator: (explane the class/file)
* Calculator holds to primitive data, x and y.
* The calculator can do basic operations on then. add, subtract, multiply, divide, etc...
**/
public class Calculator {

/**
* This is a Constructor.
* Makes an instance of the object type Calculator.
* has two atrributes with default value of x = 0 and y = 0.
**/
public Calculator{
x = 0;
y = 0;
}

/**
* This method has two parameters, x and y. It return sum of the two parameters.
*
* @param x - First number.
* @param y - Second number.
* @return - The sum from x and y.
**/
public int add(x,y){
return x + y;
}

}
```

Below is a Screenshot of what our Documentation looks like!

<p align="center">
  <img width="541" alt="Screenshot 2023-05-03 at 02 22 54" src="https://user-images.githubusercontent.com/98469510/235805887-6de7f9e9-5b7a-46ea-9ea0-45ed4fe27c09.png">
<img width="572" alt="Screenshot 2023-05-03 at 02 23 29" src="https://user-images.githubusercontent.com/98469510/235805946-3db87971-3df5-4fbc-a2a2-7a480c9701da.png">
</p>

## Final Application  
The final application turned out slightly different from what we expected, but at least we got all the core functionalities, as well as some extra functionalities, such as a blocked list, and unread messages.

### Login and Signup

<p align="center">
<img width="349" alt="Screenshot 2023-05-03 at 02 47 17" src="https://user-images.githubusercontent.com/98469510/235808526-9116d175-a063-4172-93ae-c525f3c9df6e.png">
  <img width="349" alt="Screenshot 2023-05-03 at 02 45 23" src="https://user-images.githubusercontent.com/98469510/235808344-ed79ad77-1ab3-4ded-8820-e99f8287a966.png">
</p>

### Home Screens

<p align="center">
<img width="349" alt="Screenshot 2023-05-03 at 02 49 05" src="https://user-images.githubusercontent.com/98469510/235808708-7c3d30c4-6dd6-4d38-865e-8b2406c4267b.png">
<img width="349" alt="Screenshot 2023-05-03 at 02 49 18" src="https://user-images.githubusercontent.com/98469510/235808738-4b06fb8b-ed8c-415c-b3a3-3783becedaef.png">
<img width="349" alt="Screenshot 2023-05-03 at 02 48 31" src="https://user-images.githubusercontent.com/98469510/235808643-e834705e-b0d4-4d1d-87c0-a7384abd87f0.png">
<img width="349" alt="Screenshot 2023-05-03 at 02 49 39" src="https://user-images.githubusercontent.com/98469510/235808789-673e9f0b-40e8-4f63-8635-e10f5a0c8e36.png">
<img width="349" alt="Screenshot 2023-05-03 at 02 49 54" src="https://user-images.githubusercontent.com/98469510/235808814-86659c5f-dbdc-4a30-839e-6f00071aebe5.png">
</p>

### Chat and Room Screens

<p align="center">
<img width="349" alt="Screenshot 2023-05-03 at 02 50 54" src="https://user-images.githubusercontent.com/98469510/235808904-9481cbce-41e6-4195-9ed1-7d7ff8341e37.png">
<img width="349" alt="Screenshot 2023-05-03 at 02 51 21" src="https://user-images.githubusercontent.com/98469510/235808948-e73ec69f-e589-4d63-a5e8-e4137b8c08a0.png">
</p>

What the other user sees:

<p align="center">
<img width="345" alt="Screenshot 2023-05-03 at 02 51 49" src="https://user-images.githubusercontent.com/98469510/235808991-6bbdd6ba-9be7-453a-976c-f4ce8e45b8fa.png">
</p>

Other users message:

<p align="center">
<img width="350" alt="Screenshot 2023-05-03 at 02 52 51" src="https://user-images.githubusercontent.com/98469510/235809125-fcb42e21-a6dd-4ead-b2a7-0c65522c9657.png">
</p>

### Editing or Creating A Chat Room

<p align="center">
<img width="350" alt="Screenshot 2023-05-03 at 02 53 54" src="https://user-images.githubusercontent.com/98469510/235809251-12bc8007-0048-46c1-b199-011c4f622324.png">
<img width="350" alt="Screenshot 2023-05-03 at 02 55 34" src="https://user-images.githubusercontent.com/98469510/235809427-6ad4d53f-fbdd-465e-ab03-fd148c4172da.png">
</p>

### Searching and Viewing A Users Profile

<p align="center">
<img width="350" alt="Screenshot 2023-05-03 at 02 56 31" src="https://user-images.githubusercontent.com/98469510/235809535-ed0505e0-25ec-4b70-b17b-cf585ec98b90.png">
<img width="350" alt="Screenshot 2023-05-03 at 03 06 00" src="https://user-images.githubusercontent.com/98469510/235810477-b70ba65a-7460-44a3-a3e1-cca4edc9773e.png">
</p>

### Blocked Messages

<p align="center">
<img width="350" alt="Screenshot 2023-05-03 at 03 07 30" src="https://user-images.githubusercontent.com/98469510/235810608-75c8b56c-efbb-4dca-bef7-24f83dac0130.png">
</p>
