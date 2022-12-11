# CSYE7200-Final-Project
## Abstract
The goal of the project is to stream tweets everyday to extract wordle results of users for that day (each day's wordle) and find the top 10 users for each user who match their wordle patterns, to see who has estimated or guessed the word in the same pattern that the user did, in light of the general curiosity of users who share their wordle results.

## Methodology
1. Streamed tweets through twint everyday and stored results in mysql database, preprocessed the streamed tweets to calculate all pairs user distances to find top 10 users who played similarly for a given user.
2. Data extracted via twint was extremely raw; unicode formatted data. A series of transformations done to compute wordle sequence vectors, which were used to compute distances.
3. Tweets acquired by twint API were in JSON format. Built a sample JSON parser to correctly parse the input.

## Instructions to run project
* Prerquisites: Zookeeper, Kafka, Mysql
* Instructions to install kafka: https://kafka.apache.org/quickstart
* Start Kafka server
* Run producer.py from kafka-streamGen, to start scraping tweets using twint
* Run the main function in streaming.stream.StreamTweets.scala. This will stream tweets from the kafka topic and store processes tweets in the mysql database
* Run the main function in streaming.stream.ProcessTweets.scala. This will calculate all pairs user distances and store the results to be used for visualization.

## Running Databricks visualization:
Databricks Community Edition does not seem to make sharing notebooks between users simple. The following instructions describe how to access Databricks, create the compute cluster, run the notebook, and visualize the dashboard.

Navigate to https://community.cloud.databricks.com/login.html and login with the username found in the DatabricksLink.txt file. The password will be provided to the Professor and TA privately.

Once you have logged into Databricks, navigate to the compute tab and create a new compute cluster. In the community edition, a new cluster needs to be created after 2 hours of inactivity. It may take up to 5 minutes for the cluster to be created.

![CreateCluster](https://user-images.githubusercontent.com/115491587/206866688-6fedc82c-5b3d-4b87-8716-7494589621c8.JPG)

Next, navigate to the notebook by clicking "Workspace" -> "Users" -> "CSYE-7200 Project"

![Databricks navigation](https://user-images.githubusercontent.com/115491587/206866874-1a2e386d-66d1-4484-afe9-c719bdfe22d4.JPG)

You should now be able to view and run the code within the Databricks notebook.

![Notebook](https://user-images.githubusercontent.com/115491587/206866973-d36a1338-1770-4293-84ae-06a74459d038.JPG)

To view the dashboard click "View" and under "Dashboards" select "FinalProject".

![DashboardSelection](https://user-images.githubusercontent.com/115491587/206867137-67b92bdf-4133-4a46-a915-a4165ca7f269.JPG)

Once the dashboard is open, you should be seeing the top 10 most similar Wordle results for the user whose username was entered in the textbox at the top of the dashboard. Manually narrow the columns until they are only wide enough to display 5 blocks. Click the arrow next to the Wordle pattern to visualize it in the 5 x 6 matrix we are used to seeing. This is a work around for the fact that you cannot set fixed column widths in Databricks. Entering a different username in the textbox will cause the dashboard to refresh and the width of the columns will need to be manually set again.  

![Databricks Dashboard](https://user-images.githubusercontent.com/115491587/206867281-6f1c9b5c-026e-4abb-ab40-86a27ff56058.JPG)
