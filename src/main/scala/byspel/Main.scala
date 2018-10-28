package byspel

import app.{DatabaseApi, DatabaseApp, HttpApp}

object Main
    extends DatabaseApp
    with HttpApp
    with DatabaseApi
    with Service
    with Ui
    with Migrations
    with Inserts
