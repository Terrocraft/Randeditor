# Randeditor Plugin for CityBuild Minecraft Servers


<div align="center">
  <img src="https://img.shields.io/github/languages/code-size/Terrocraft/Randeditor" alt="GitHub code size in bytes"/>                     <img src="https://img.shields.io/endpoint?url=https://ghloc.vercel.app/api/Terrocraft/Randeditor/badge?filter=.java$&label=lines%20of%20code&color=blue" alt="GitHub lines of code"/>
</div>

## ------------------This is a Dev Build!!!!------------------

## Overview
The **Randeditor Plugin** is a powerful tool designed specifically for CityBuild Minecraft servers, enabling server admins to create and customize player inventory layouts with ease. This plugin allows admins to pre-configure a special inventory, which players can access and use to modify the borders (or "rands") of their plots. This feature enhances the gameplay experience by giving players the ability to personalize the edges of their plots, while ensuring they have access to the exact items chosen by the admin.

German Tutorial:
https://youtu.be/HY4DkUITdmA?si=KoyBBcKJxzRlIE5k
## Features

### Custom Inventory Setup for Admins
Admins can create and design a specific inventory that is tailored to the needs of their server. This inventory layout can include any items or blocks the admin deems necessary for players to use when editing their plot borders.

### Simple Command Interface
<ul>
  <li>
    <code>/randeditor set</code>: Admins can use this command to save the custom inventory they’ve created. This inventory will then be the template that players receive when they enter the Randeditor mode.
  </li>
  <li>
    <code>/randeditor</code>: Players can access the Randeditor mode by entering this command on their plot. Their current inventory is temporarily saved, and they are provided with the admin-configured inventory to edit the borders of their plot.
  </li>
</ul>

### Permissions System
The plugin utilizes a permission-based system to control access to the Randeditor features:
<ul>
  <li>
    <strong>randcustomizer.randeditmode</strong>: Grants players the ability to enter the Randeditor mode and customize their plot borders.
  </li>
  <li>
    <strong>randcustomizer.randeditmode.set</strong>: Allows admins to set and save the custom inventory layout that players will use in Randeditor mode.
  </li>
</ul>

### Player Inventory Management
When a player enters Randeditor mode, their existing inventory is stored temporarily. They are given the specially prepared inventory by the admin to modify the plot borders. Once they finish editing, or leave the plot, their original inventory is restored, ensuring no items are lost in the process.

### Plot Border Customization
Players can use the provided inventory to completely personalize the borders of their plots. This feature encourages creativity, as players can experiment with different block types and designs to make their plot stand out from others.

### Seamless Exit and Re-Entry
Players can exit the Randeditor mode at any time by either leaving their plot or by re-entering the <code>/randeditor</code> command. Upon exiting, their original inventory is restored automatically, allowing them to continue their gameplay without interruption.

## Use Case Scenarios
<ul>
  <li>
    <strong>Individualized Plot Design</strong>: Empower players to fully customize the borders of their plots, allowing for unique and creative designs that reflect their personal style.
  </li>
  <li>
    <strong>Enhanced Creative Expression</strong>: Encourage players to experiment with different block combinations and layouts to make their plot edges as creative and diverse as possible.
  </li>
  <li>
    <strong>Server-Wide Creativity Boost</strong>: By enabling players to freely customize their plot borders, the plugin fosters a more vibrant and creative community within your server.
  </li>
</ul>

## Alpha Release Disclaimer
<p>
  <strong>Note:</strong> The Randeditor Plugin is currently in its Alpha stage. While the core features are functional, there may still be bugs or unexpected behavior. We appreciate your understanding and encourage feedback to help improve the plugin as it develops.
</p>

## Conclusion
The Randeditor Plugin is an essential addition to any CityBuild Minecraft server looking to improve player engagement and creativity. By giving admins the power to design custom inventories and providing players with an easy way to fully personalize their plot borders, this plugin adds a new layer of depth to the CityBuild experience. Whether for aesthetic purposes, creative expression, or simply to give players more control over their plot designs, Randeditor is the perfect tool to enhance your server.

## bStats
[![bStats Graph Data](https://bstats.org/signatures/bukkit/RandCustomizer.svg)](https://bstats.org/plugin/bukkit/RandCustomizer)
