# doc-converter

Doc-converter is a Restfull service that uses [Jod converter library](https://github.com/sbraconnier/jodconverter/) and [LibreOffice](https://www.libreoffice.org/) to provide a service for converting documents.

# Formats (to and from)

- pdf
- html
- odt
- doc
- docx
- rtf
- txt
- csv
- and all other formats supported by LibreOffice
    
# Docker

`sudo docker run -p 6511:6511 -e LO_PORTS=<LO_PORTS> <IMAGE>`

* LO_PORTS: indicates how many libreoffice instances (threads) will be created

# Example

* Converting DOCX document into PDF (post)
`http://localhost:6511/jod/docx/pdf`