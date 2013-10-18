# Using a browser to tunnel into a Hadoop cluster to inspect worker node logs.

Adnan Al-Alawiyat & Brad Rubin 10/17/2013

### In order to inspect work node logs in a Hadoop cluster that is behind a firewall with only SSH access, a browser must be setup for tunneling.
---
Our Hadoop cluster is behind a firewall, and only the SSH port (22) is open.  Furthermore, our worker nodes use private IP addressing.  The technique described here will allow a browser to access the cluster via an SSH tunnel.  This not only allows access to the JobTracker web page for status, but also allows drill down to logs on individual worker nodes.  We must also tunnel the DNS requests to the cluster to allow the worker node names to resolve to their private IP addresses via the hosts file.  With Firefox, you must dedicate the browser for cluster use.  With Chrome, you can use the browser for both the cluster and normal web browsing.  We describe instuctions for Windows, Mac, and Linux.

## Using Firefox on Windows

1. Set up a SOCKS local server via an SSH connection to cluster, which would forward traffic from port 9537 to the cluster, as described in PuTTY Setup section.  *Note: Any unused port can be used for this.*

2. Follow the instructions for Firefox Setup.


## Using Firefox on Mac/Linux

1. Set up a SOCKS local server via an SSH connection to cluster, which would forward traffic from port 9537 to the cluster, by running the following command:

    ``
    ssh -D 9537 -C <user_name>@hc.gps.stthomas.edu
    ``

2. 	Follow the instructions for Firefox Setup.	
	

## Firefox Setup

1. Navigate to Firefox Settings/Preferences > Advanced > Network
2. Click on Settings button on the right of Connection section  

	![Firefox settings > network tab][1]

3. In Connection Settings window:
    - Check "Manual proxy configurations"
    - For SOCKS Host , enter "127.0.0.1"
    - For SOCKS Host port, enter "9537"
    - Select "SOCKS v5" and select OK  
    
	![Firefox Connection Settings > Manual proxy configurationn][2]

4. In Firefox address bar, Enter "about:config" and confirm to prompt to be careful:  

	![Firefox change advanced settings confirmationn][3]  
	
5. Search for ``network.proxy.socks_remote_dns`` and toggle to true:  

	![Firefox enable remote DNS][4]

6. In Firefox go to http://hc.gps.stthomas.edu:50030  

*Note: After following steps above, Firefox is now dedicated to tunneling, even after a restart.*
 
To disable tunneling for Firefox:  

1. Navigate to Firefox Settings/Preferences > Advanced > Network
2. Click on Settings button on the right of Connection section 
 
	![Firefox settings > network tab][1]

3. In Connection Settings window:
    - Check "Use system proxy settings"  
    
	![Firefox use system proxy settings ][5]

4. In Fiefox address bar, Enter "about:config" and confirm to prompt to be careful:  

	![Firefox change advanced settings confirmation][3]

5. Search for ``network.proxy.socks_remote_dns`` and toggle to false:  

	![Firefox disable remote DNS][6]




## PuTTY Setup

1. Start Putty and enter the following in HostName field, where **user_name** is your login name:  
 user_name@hc.gps.stthomas.edu
2. Accept 22 as default port number:  

	![basic options for your PuTTY session window][7]  

3. On left hand side, expand SSH category and click on Tunnels:  

	![Options controlling SSH port forwarding window][8]
4. In Tunnels settings window, enter the following settings, then select "Add"  

	**Source port**: 9537  
	**Destination**: hc.gps.stthomas.edu  
	Select **Dynamic** and **Auto** radio buttons:  

	![Filled out Options controlling SSH port forwarding window][9]
	
5. Finally select **Open** button and enter your password


## Using Chrome on Windows

1. Set up a SOCKS local server via an SSH connection to cluster, which would forward traffic from port 9537 to the cluster, as described in PuTTY Setup section.

2.  Make sure Chrome is not currently running and start a new instance from command line that uses proxy server from step 1 above:

    ``
    %CHROME_BIN% --proxy-server="socks://127.0.0.1:9537"
    ``   
    
*Note: Chrome will use proxy server only for current running instance. After Chrome is restarted, it will operate as normal without a proxy.*


## Using Chrome on Linux

1. Set up a SOCKS local server via SSH connection to cluster, which would forward traffic from port 9537 to the cluster, by running the following command:

    ``
    ssh -D 9537 -C <user_name>@hc.gps.stthomas.edu
    ``

2.  Make sure Chrome is not currently running and start a new instance from command line that uses proxy server from step 1 above:

    ``
    google-chrome --proxy-server="socks://127.0.0.1:9537"
    ``   
    
*Note: Chrome will use proxy server only for current running instance. After Chrome is restarted, it will operate as normal without a proxy.*
    
## Using Chrome on Mac

1. Set up a SOCKS local server via SSH connection to cluster, which would forward traffic from port 9537 to the cluster, by running the following command:

    ``
    ssh -D 9537 -C <user_name>@hc.gps.stthomas.edu
    ``

2. Make sure Chrome is not currently running and start a new instance from command line that uses proxy server from step 1 above:

    ``
    /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --proxy-server="socks://127.0.0.1:9537"
    ``   
    
*Note: Chrome will use proxy server only for current running instance. After Chrome is restarted, it will operate as normal  without a proxy.*

  [1]: images/firefox_advanced_network_settings.png
  [2]: images/firefox_manual_proxy_configuration.png
  [3]: images/firefox_be_careful_warning.png
  [4]: images/firefox_enable_remote_dns.png
  [5]: images/firefox_use_system_proxy_settings.png
  [6]: images/firefox_disable_remote_dns.png
  [7]: images/PuTTYHostAndPort.png
  [8]: images/PuTTY_SSH_Tunnels.png
  [9]: images/FilledOptionsControllingSSHPortForwarding.png