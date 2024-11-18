(function(apiKey){
    (function(p,e,n,d,o){var v,w,x,y,z;o=p[d]=p[d]||{};o._q=o._q||[];
    v=['initialize','identify','updateOptions','pageLoad','track'];for(w=0,x=v.length;w<x;++w)(function(m){
        o[m]=o[m]||function(){o._q[m===v[0]?'unshift':'push']([m].concat([].slice.call(arguments,0)));};})(v[w]);
        y=e.createElement(n);y.async=!0;y.src='/api/v1/dotexp/pendo.js';
        z=e.getElementsByTagName(n)[0];z.parentNode.insertBefore(y,z);})(window,document,'script','pendo');
})('pendoId');


function initPendo(data) {

    const visit = {
        visitor: {
            id: data.user.userHash,   // Required if user is logged in, default creates anonymous ID
            email: data.user.email,
            full_name: data.user.fullName,
            role: (data.user.isAdmin) ? "Admin" : "user"
        },

        account: {
            id: data.clusterId, // Required if using Pendo Feedback, default uses the value 'ACCOUNT-UNIQUE-ID'
            name: data.licenseName,
            is_paying: (!data.licenseTrial),
            licenseLevel: data.licenseLevel,
            licenseSerial: data.licenseSerial,
            licenseType: data.licenseType
        }
    };

    console.log('installing visit:', visit);

    pendo.initialize(visit);
}

async function tryPendo() {
    console.log('trying pendo');
    try {
        const response = await fetch('/api/v1/dotexp/visit');
        const data = await response.json();
        if (data.user === undefined) {
            console.log("trying pendo: not logged in");
            return;
        }
        initPendo(data);
        myStopFunction();

    } catch (error) {
        console.log("trying pendo error:", error);
    }
}

const myTimeout = setInterval(tryPendo, 5000);

function myStopFunction() {
    console.log("trying pendo: finished");
    clearInterval(myTimeout);

}
