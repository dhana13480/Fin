ags = "RMCS"
region = "east"

tags {
    ags = "RMCS"
    costCenter = "RSM302"
    sdlc = "DEV"
    owner = "Sachin Goel"
}

deliveryStream {
    enableHttpStream = true  
}

environments {
    dev {
        tags {
            sdlc = "DEV"
        }
    }

    qa {
        tags {
            sdlc = "QA"
        }
    }

    prod {
        tags {
            sdlc = "PROD"
        }
    }
}
