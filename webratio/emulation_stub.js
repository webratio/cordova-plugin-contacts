function createStubs() {

    var contacts = null;
    var isHidden = true;
    var $ = window.top.jQuery;

    var fakeContact = {
        "id": "24",
        "name": {
            "givenName": "",
            "familyName": ""
        },
        "addresses": [ {
            "type": "home",
            "streetAddress": "9381 Oak Avanue",
            "locality": "Tucson",
            "region": "Texas",
            "country": "US",
            "postalCode": "76061"
        }, {
            "type": "work",
            "streetAddress": "6799 Union Street",
            "locality": "Philadelphia",
            "region": "Pennsylvania",
            "country": "US",
            "postalCode": "19146"
        }, {
            "type": "other",
            "streetAddress": "P.O. Box 566, 7037 Nec, Av.",
            "locality": "Arlington",
            "region": "Iowa",
            "country": "US",
            "postalCode": "48329"
        } ],
        "phoneNumbers": [ {
            "type": "home",
            "value": "(184) 520-6818"
        }, {
            "type": "work",
            "value": "(475) 280-9681"
        }, {
            "type": "mobile",
            "value": "1-540-427-4321"
        }, {
            "type": "fax",
            "value": "(771) 177-7525"
        }, {
            "type": "main",
            "value": "(700) 149-8300"
        }, {
            "type": "other",
            "value": "(543) 698-9893"
        }, {
            "type": "other",
            "value": "(103) 353-5171"
        } ],
        "emails": [ {
            "type": "home",
            "value": "aptent.taciti@et.net"
        }, {
            "type": "work",
            "value": "nec.metus@aliquet.com"
        }, {
            "type": "other",
            "value": "orci@eget.co.uk"
        } ]
    };

    function initPick() {

        $('#wr-contacts-emulator').remove();

        $('#platform-events-fire-back').css("display", "none");
        $('#platform-events-fire-suspend').before("<button id=\"platform-events-fire-back-contacts\">Back</button>");
        $('#platform-events-fire-back-contacts').button().css("width", "90px");

        var liIndex = "<li style=\"  border-bottom: solid #000 2px; padding: 10px; font-weight: 70\">";
        var liName = "<li style=\"cursor: pointer; padding: 10px; \"";
        // var liMultiName = "<li style=\"cursor: pointer; padding: 5% 0 5% 5%; border: solid #E6E6E6 1px; border-left: 0;
        // border-right: 0; border-top: 0;\"";
        var pickContactsTemplate = [
                "<section id=\"wr-contacts-emulator\" style=\"display:none; background: rgba(0, 0, 0, 0); position: absolute; width: 100%; height: 100%; z-index: 10000;\">",
                "<div style=\"background: #fff; height: 100%; width: 100%; overflow: auto; \">",
                "<div id=\"wr-contacts-title\" style=\"background: #000; font-size: 1em; color: #fff; font-weight: bold; line-height: 44px;padding: 0 10px;position: absolute;top: 0;left: 0;right: 0;\">",
                "<div style=\"display: inline-block; color: #E6E6E6; \">Find contacts</div>",
                "<div id=\"cancel\" style=\" cursor: pointer;  float: right;\">CANCEL</div>", "</div>",
                "<ul style=\"color: #000; list-style-type: none; padding: 0; margin: 44px 0;\">", liIndex, "C</li>", liName,
                "id=\"wr-contact-castor-villarreal\">", "Castor Villarreal</li>", liIndex, "E</li>", liName,
                "id=\"wr-contact-eaton-hogan\">", "Eaton Hogan</li>", liName, "id=\"wr-contact-erich-davis\">", "Erich Davis</li>",
                liIndex, "G</li>", liName, "id=\"wr-contact-gail-gilmore\">", "Gail Gilmore</li>", liIndex, "J</li>", liName,
                "id=\"wr-contact-jael-wade\">", "Jael Wade</li>", liName, "id=\"wr-contact-joshua-william\">", "Joshua William</li>",
                liName, "id=\"wr-contact-juliet-kaufman\">", "Juliet Kaufman</li>", liIndex, "K</li>", liName,
                "id=\"wr-contact-kieran-bridges\">", "Kieran Bridges</li>", liName, "id=\"wr-contact-kimberly-nolan\">",
                "Kimberly Nolan</li>", liIndex, "L</li>", liName, "id=\"wr-contact-lael-jordan\">", "Lael Jordan</li>", liIndex,
                "M</li>", liName, "id=\"wr-contact-micah-mclaughlin\">", "Micah Mclaughlin</li>", liName,
                "id=\"wr-contact-mia-schneider\">", "Mia Schneider</li>", liIndex, "P</li>", liName, "id=\"wr-contact-philip-yang\">",
                "Philip Yang</li>", liIndex, "S</li>", liName, "id=\"wr-contact-sonia-branch\">", "Sonia Branch</li>", liIndex,
                "V</li>", liName, "id=\"wr-contact-vladimir-burns\">", "Vladimir Burns</li>", "</div>", "</section>" ].join("\n");

        var pickContacts = $(pickContactsTemplate);
        $('#overlay-views').append(pickContacts);
        return pickContacts;
    }

    function initSave(contact) {

        $('#wr-contacts-emulator').remove();

        $('#platform-events-fire-back').css("display", "none");
        $('#platform-events-fire-suspend').before("<button id=\"platform-events-fire-back-contacts\">Back</button>");
        $('#platform-events-fire-back-contacts').button().css("width", "90px");

        var saveContactsTemplate = [
                "<section id=\"wr-contacts-emulator\" style=\"background: #fff; display:none; position: absolute; width: 100%; height: 100%; z-index: 10000; \">",
                "<div style=\"background: #fff; height: 100%; width: 100%; overflow: auto;\">",
                "<div id=\"wr-contacts-title\" style=\"background: #000; font-size: 1em; color: #fff; font-weight: bold; line-height: 44px;padding: 0 10px;position: absolute;top: 0;left: 0;right: 0;\">",
                "<div id=\"done\" style=\"display: inline-block; cursor: pointer; \">DONE</div>",
                "<div id=\"cancel\" style=\" cursor: pointer;  float: right;\">CANCEL</div>", "</div>",
                "<div id=\"wr-contacts-fields\" style=\"width: 100%; font-size: 0.8em; margin: 54px 0;\"></div>", "</div>",
                "</section>" ].join("\n");

        var saveContacts = $(saveContactsTemplate);

        $('#overlay-views').append(saveContacts);

        var elem, type;

        /* Create Name field */
        elem = [
                "<table id=\"contacts-name\" style=\"table-layout: fixed; width: 100%; border-spacing: 10px; margin-bottom: 5px; \">",
                "<caption style=\"padding: 5px 10px; text-align: left; font-weight: bold;  border-bottom: 1px solid #ccc;  \">",
                "NAME", "</caption>", "</table>" ].join("\n");

        $('#wr-contacts-fields').append($(elem));

        var name = "";
        if (contact.name.givenName) {
            name = contact.name.givenName;
            if (contact.name.familyName) {
                name = name + " " + contact.name.familyName;
            }
        } else {
            if (contact.name.familyName) {
                name = contact.name.familyName + " " + contact.name.familyName;
            }
        }

        elem = [ "<tr style=\"width: 100%;\">",
                "<td id=\"contact-name-value\" style=\"width: 70%; overflow: hidden; text-overflow: ellipsis; \">", name, "</td>",
                "</tr>" ].join("\n");

        $('#contacts-name').append($(elem));

        if (name === "") {
            $('#contact-name-value').html("Name");
            $('#contact-name-value').css("color", "#BDBDBD");
        }

        /* Create Phone Numbers fields */
        elem = [
                "<table id=\"contacts-phones\" style=\"table-layout: fixed; width: 100%; border-spacing: 10px; margin-bottom: 5px; \">",
                "<caption style=\"padding: 5px 10px; text-align: left; font-weight: bold;  border-bottom: 1px solid #ccc;  \">",
                "PHONE", "</caption>", "</table>" ].join("\n");

        $('#wr-contacts-fields').append($(elem));

        if (contact.phoneNumbers) {
            for (obj in contact.phoneNumbers) {
                elem = [ "<tr style=\"width: 100%;\">", "<td style=\"width: 15%; color: #888; \">",
                        contact.phoneNumbers[obj].type.toUpperCase(), "</td>",
                        "<td style=\"width: 70%; overflow: hidden; text-overflow: ellipsis; \">", contact.phoneNumbers[obj].value,
                        "</td>", "</tr>" ].join("\n");
                $('#contacts-phones').append($(elem));
            }
        } else {
            elem = [
                    "<tr style=\"width: 100%;\">",
                    "<td style=\"width: 15%; color: #888; \">",
                    "TYPE",
                    "</td>",
                    "<td id=\"contact-empty-elem\" style=\"width: 70%; overflow: hidden; text-overflow: ellipsis; color: #BDBDBD; \">",
                    "Number", "</td>", "</tr>" ].join("\n");
            $('#contacts-phones').append($(elem));
        }

        /* Create Emails fields */
        elem = [
                "<table id=\"contacts-emails\" style=\"table-layout: fixed; width: 100%; border-spacing: 10px; margin-bottom: 5px; \">",
                "<caption style=\"padding: 5px 10px; text-align: left; font-weight: bold;  border-bottom: 1px solid #ccc;  \">",
                "EMAIL", "</caption>", "</table>" ].join("\n");

        $('#wr-contacts-fields').append($(elem));

        if (contact.emails) {
            for (obj in contact.emails) {
                elem = [ "<tr style=\"width: 100%;\">", "<td style=\"width: 15%; color: #888; \">",
                        contact.emails[obj].type.toUpperCase(), "</td>",
                        "<td style=\"width: 70%; overflow: hidden; text-overflow: ellipsis; \">", contact.emails[obj].value, "</td>",
                        "</tr>" ].join("\n");
                $('#contacts-emails').append($(elem));
            }
        } else {
            elem = [
                    "<tr style=\"width: 100%;\">",
                    "<td style=\"width: 15%; color: #888; \">",
                    "TYPE",
                    "</td>",
                    "<td id=\"contact-empty-elem\" style=\"width: 70%; overflow: hidden; text-overflow: ellipsis; color: #BDBDBD; \">",
                    "Email", "</td>", "</tr>" ].join("\n");
            $('#contacts-emails').append($(elem));
        }

        /* Create Addresses fields */
        elem = [
                "<table id=\"contacts-addresses\" style=\"table-layout: fixed; width: 100%; border-spacing: 10px; margin-bottom: 5px; \">",
                "<caption style=\"padding: 5px 10px; text-align: left; font-weight: bold;  border-bottom: 1px solid #ccc;  \">",
                "ADDRESS", "</caption>", "</table>" ].join("\n");

        $('#wr-contacts-fields').append($(elem));

        if (contact.addresses) {
            for (obj in contact.addresses) {
                elem = [ "<tr style=\"width: 100%;\">", "<td style=\"width: 15%; color: #888; \">",
                        contact.addresses[obj].type.toUpperCase(), "</td>",
                        "<td style=\"width: 70%; overflow: hidden; text-overflow: ellipsis; \">",
                        "<ul style=\"list-style-type: none; margin: 0; padding: 5px; \">", "<li>",
                        contact.addresses[obj].streetAddress, "</li>", "<li>", contact.addresses[obj].locality, "</li>", "<li>",
                        contact.addresses[obj].postalCode, "</li>", "<li>", contact.addresses[obj].country, "</li>", "</ul>", "</td>",
                        "</tr>" ].join("\n");

                $('#contacts-addresses').append($(elem));
            }
        } else {
            elem = [
                    "<tr style=\"width: 100%;\">",
                    "<td style=\"width: 15%; color: #888; \">",
                    "TYPE",
                    "</td>",
                    "<td id=\"contact-empty-elem\" style=\"width: 70%; overflow: hidden; text-overflow: ellipsis; color: #BDBDBD; \">",
                    "Address", "</td>", "</tr>" ].join("\n");
            $('#contacts-addresses').append($(elem));
        }

        return saveContacts
    }

    return {
        Contacts: {
            pickContact: function() {
                contacts = initPick();
                var p = new Promise(function(resolve, reject) {

                    $('#platform-events-fire-back-contacts').click(function(e) {
                        var contact = {
                            "code": 0
                        };
                        $('#platform-events-fire-back-contacts').remove();
                        $('#platform-events-fire-back').css("display", "");
                        getContactInfos(contact);
                    });

                    $('#wr-contacts-emulator #cancel').click(function(e) {
                        var contact = {
                            "code": 0
                        };
                        getContactInfos(contact);
                    });

                    $('#wr-contact-juliet-kaufman').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Juliet";
                        fakeContact.name.familyName = "Kaufman";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-kieran-bridges').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Kieran";
                        fakeContact.name.familyName = "Bridges";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-kimberly-nolan').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Kimberly";
                        fakeContact.name.familyName = "Nolan";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-micah-mclaughlin').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Micah";
                        fakeContact.name.familyName = "Mclaughlin";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-vladimir-burns').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Vladimir";
                        fakeContact.name.familyName = "Burns";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-philip-yang').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Philip";
                        fakeContact.name.familyName = "Yang";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-gail-gilmore').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Gail";
                        fakeContact.name.familyName = "Gilmore";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-lael-jordan').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Lael";
                        fakeContact.name.familyName = "Jordan";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-sonia-branch').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Sonia";
                        fakeContact.name.familyName = "Branch";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-castor-villarreal').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Castor";
                        fakeContact.name.familyName = "Villarreal";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-eaton-hogan').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Eaton";
                        fakeContact.name.familyName = "Hogan";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-mia-schneider').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Mia";
                        fakeContact.name.familyName = "Schneider";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-erich-davis').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Erich";
                        fakeContact.name.familyName = "Davis";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-joshua-william').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Joshua";
                        fakeContact.name.familyName = "William";
                        getContactInfos(fakeContact);
                    });

                    $('#wr-contact-jael-wade').click(function(e) {
                        e.preventDefault();
                        fakeContact.name.givenName = "Jael";
                        fakeContact.name.familyName = "Wade";
                        getContactInfos(fakeContact);
                    });

                    function getContactInfos(contact) {
                        $('#platform-events-fire-back-contacts').remove();
                        $('#platform-events-fire-back').css("display", "");
                        contacts.hide('slide', {
                            direction: 'down',
                            duration: 250
                        });
                        resolve(contact);
                    }
                    contacts.show('slide', {
                        direction: 'down',
                        duration: 250
                    });
                })
                return p;
            },
            save: function(contact) {
                var contactId;

                if (contact.id) {
                    contactId = contact.id;
                }

                contacts = initSave(contact);

                var savePromise = new Promise(function(resolve, reject) {

                    $('#platform-events-fire-back-contacts').click(function(e) {
                        var contact = {
                            "code": 0
                        };
                        $('#platform-events-fire-back-contacts').remove();
                        $('#platform-events-fire-back').css("display", "");
                        getContactInfos(contact);
                    });

                    $('#wr-contacts-emulator #done').click(function(e) {
                        var contact = {};

                        if (contactId !== undefined) {
                            contact.id = contactId;
                        } else {
                            contact.id = "25";
                        }
                        getContactInfos(contact);
                    });

                    $('#wr-contacts-emulator #cancel').click(function(e) {
                        var contact = {
                            "code": 0
                        };
                        getContactInfos(contact);
                    });
                    function getContactInfos(contact) {
                        $('#platform-events-fire-back-contacts').remove();
                        $('#platform-events-fire-back').css("display", "");
                        contacts.hide('slide', {
                            direction: 'down',
                            duration: 250
                        });
                        resolve(contact);
                    }
                    contacts.show('slide', {
                        direction: 'down',
                        duration: 250
                    });
                })
                return savePromise;
            }
        }
    };
};