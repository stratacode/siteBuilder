## StrataCode siteBuilder 

This project is a bundle of StrataCode layers that implement various
configurations of running the site builder. 

To run, install StrataCode's scc command.

Go to <a href="https://www.stratacode.com/examples/siteBuilder.html">the example page</a> for instructions on how to run various configurations.

The project is organized into several layer groups:

### content

Contains base classes for storing Media, ManagedResources for generic web sites.

Adds the content.userModel layers for users building sites - SiteContext, PageManager.

### user

Layers for user profile management

### product

Product catalog, simple order management layers that plug into 
the site builder

### blog

The blog plugin layers that add basic posts, management of the blog. 

### ec

A set of wrapper layers that build a configuration of an ecommerce site builder.
