
local BASE_DIR = "/mh/PrivateCloudStorage/data"

local lfs = require("lfs")
local cjson = require("cjson")

local file_list_data = {}

function getType(path)
	return lfs.attributes(path).mode
end

function getSize(path)
	return lfs.attributes(path).size
end

function isDir(path)
	return getType(path) == "directory"
end

function findx(str,x)
	for i = 1,#str do
		if string.sub(str,-i,-i) == x then
			return -i
		end
	end
end

function getName(str)
	return string.sub(str,findx(str,"/") + 1,-1)
end

function getJson(path, t)
--[[ 	local table = "{"
	for file in lfs.dir(path) do
		p = path .. "/" .. file
		if file ~= "." and file ~= '..' then
			if isDir(p) then
				s = "{'text':'".. file .. "','type':'" .. getType(p) .. "','path':'" .. p .. "','children':[]},"
			else
				s = "{'text':'".. file .. "','type':'" .. getType(p) .. "','path':'" .. p .. "','size':" .. getSize(p) .. ",'leaf':true},"
			end  
			table = table .. s    
		end
	end
	table = table .. "}"
	return table ]]
	
	--t[path] = {}
	for f in lfs.dir(path) do
		p = path .. "/" .. f
		if f ~= "." and f ~= '..' then
			if isDir(p) then
				t[f] = {}
				getJson(p, t[f])
			else
				--s = "{'text':'".. file .. "','type':'" .. getType(p) .. "','path':'" .. p .. "','size':" .. getSize(p) .. ",'leaf':true},"
				t[f] = f
			end  
			
		end
	end
end


local function get_server_file_list()
	getJson(BASE_DIR, file_list_data)
	ngx.log(ngx.ERR, cjson.encode(file_list_data))
end


local http_method = ngx.req.get_method()

if http_method == "GET" then
	ngx.log(ngx.ERR, "a GET req")
	local args = ngx.req.get_uri_args()
	for key, val in pairs(args) do
		if type(val) == "table" then
			ngx.log(ngx.ERR, "not support")
			ngx.exit(403)
		else
			if key == "action" then
				if val == "get_server_file_list" then
					get_server_file_list()
				end
			end
		end
	end
	
end


if http_method == "POST" then
	ngx.log(ngx.ERR, "a POST req")
end