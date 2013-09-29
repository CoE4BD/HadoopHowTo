# Using a browser to tunnel into a Hadoop cluster to inspect worker node logs

Adnan Al-Alawiyat & Bradley S. Rubin, PhD 9/27/2013

### When a Hadoop cluster is behind a firewall with only SSH access, in order to inspect logs in worker nodes that use private IP address, a browser must be setup for tunneling.
---
Our Hadoop cluster is behind a firewall, and only the SSH port (22) is open.  Furthermore, our worker nodes use private IP addressing.  The technique described here will allow a browser to access the cluster via a SSH tunnel.  This not only allows access to the JobTracker web page for status, but also allows drilldown to logs on individual worker nodes.  We must also tunnel the DNS requests to the cluster to allow the worker node names to resolve to their private IP addresses via the hosts file.

## Using Firefox on Windows

## Using Firefox on Mac/Linux

## Using Chrome on Windows

## Using Chrome on Linux

## Using Chrome on Mac

``
This is how you do a command line box
``

This is how you do an image
![Image name](images/sample.png)

This is how you do a web link
[UST](http://www.stthomas.edu)

*This is how you do a note*