function createStubs() {

    return {
        Contacts: {
            pickContact: function() {
                console.log("contact picked");
            },
            save: function() {
                console.log("contact saved");
            }
        }
    };
};